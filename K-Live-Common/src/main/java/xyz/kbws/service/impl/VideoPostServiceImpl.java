package xyz.kbws.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.kbws.common.ErrorCode;
import xyz.kbws.constant.CommonConstant;
import xyz.kbws.constant.MqConstant;
import xyz.kbws.constant.UserConstant;
import xyz.kbws.exception.BusinessException;
import xyz.kbws.mapper.VideoFilePostMapper;
import xyz.kbws.mapper.VideoPostMapper;
import xyz.kbws.model.entity.VideoFilePost;
import xyz.kbws.model.entity.VideoPost;
import xyz.kbws.model.enums.VideoFileTransferResultEnum;
import xyz.kbws.model.enums.VideoFileTypeEnum;
import xyz.kbws.model.enums.VideoStatusEnum;
import xyz.kbws.rabbitmq.MessageProducer;
import xyz.kbws.redis.RedisComponent;
import xyz.kbws.service.VideoFilePostService;
import xyz.kbws.service.VideoPostService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author fangyuan
 * @description 针对表【videoPost(已发布视频信息表)】的数据库操作Service实现
 * @createDate 2024-11-28 20:36:20
 */
@Service
public class VideoPostServiceImpl extends ServiceImpl<VideoPostMapper, VideoPost>
        implements VideoPostService {

    @Resource
    private VideoFilePostService videoFilePostService;

    @Resource
    private VideoFilePostMapper videoFilePostMapper;

    @Resource
    private RedisComponent redisComponent;

    @Resource
    private MessageProducer messageProducer;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addVideoPost(VideoPost videoPost, List<VideoFilePost> videoFilePosts) {
        if (videoFilePosts.size() > redisComponent.getSystemSetting().getVideoCount()) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        videoPost.setId(RandomUtil.randomNumbers(UserConstant.LENGTH_10));
        videoPost.setStatus(VideoStatusEnum.STATUS0.getValue());
        this.save(videoPost);

        int index = 1;
        for (VideoFilePost videoFilePost : videoFilePosts) {
            videoFilePost.setFileIndex(index++);
            videoFilePost.setVideoId(videoPost.getId());
            videoFilePost.setUserId(videoPost.getUserId());
            videoFilePost.setFileId(RandomUtil.randomString(CommonConstant.LENGTH_20));
            videoFilePost.setUpdateType(VideoFileTypeEnum.UPDATE.getValue());
            videoFilePost.setTransferResult(VideoFileTransferResultEnum.TRANSFER.getValue());
        }
        videoFilePostService.saveOrUpdateBatch(videoFilePosts);

        for (VideoFilePost item : videoFilePosts) {
            item.setUserId(videoPost.getUserId());
            item.setVideoId(videoPost.getId());
        }
        // 发送视频转码消息到消息队列
        JSONArray jsonArray = JSONUtil.parseArray(videoFilePosts);
        messageProducer.sendMessage(MqConstant.FILE_EXCHANGE_NAME, MqConstant.TRANSFER_VIDEO_ROOTING_KEY, jsonArray.toString());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateVideoPost(VideoPost videoPost, List<VideoFilePost> videoFilePosts) {
        if (videoFilePosts.size() > redisComponent.getSystemSetting().getVideoCount()) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        VideoPost post = this.getById(videoPost.getId());
        if (post == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "该视频不存在");
        }
        if (ArrayUtils.contains(new Integer[]{VideoStatusEnum.STATUS0.getValue(), VideoStatusEnum.STATUS2.getValue()}, post.getStatus())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        List<VideoFilePost> deleteFileList = new ArrayList<>();
        List<VideoFilePost> addFileList = new ArrayList<>();
        QueryWrapper<VideoFilePost> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("videoId", videoPost.getId());
        queryWrapper.eq("userId", post.getUserId());
        List<VideoFilePost> dbVideoFilePosts = videoFilePostService.list(queryWrapper);
        Map<String, VideoFilePost> uploadFileMap = videoFilePosts.stream()
                .collect(Collectors.toMap(
                        VideoFilePost::getUploadId,
                        Function.identity(),
                        (data1, data2) -> data2
                ));
        boolean changeFileName = false;
        for (VideoFilePost item : dbVideoFilePosts) {
            VideoFilePost updateFile = uploadFileMap.get(item.getUploadId());
            if (updateFile == null) {
                deleteFileList.add(item);
            } else if (!updateFile.getFileName().equals(item.getFileName())) {
                changeFileName = true;
            }
        }
        addFileList = videoFilePosts.stream()
                .filter(item -> item.getFileId() == null)
                .collect(Collectors.toList());
        boolean changeVideo = this.changeVideo(videoPost);
        if (!addFileList.isEmpty()) {
            videoPost.setStatus(VideoStatusEnum.STATUS0.getValue());
        } else if (changeVideo || changeFileName) {
            videoPost.setStatus(VideoStatusEnum.STATUS2.getValue());
        }
        this.updateById(videoPost);
        if (!deleteFileList.isEmpty()) {
            List<String> delFileIdList = deleteFileList.stream().map(VideoFilePost::getFileId).collect(Collectors.toList());
            videoFilePostMapper.deleteBatchByFileId(delFileIdList, videoPost.getUserId());
            List<String> delFilePathList = deleteFileList.stream().map(VideoFilePost::getFilePath).collect(Collectors.toList());
            // 发送删除文件消息到消息队列
            JSONArray jsonArray = JSONUtil.parseArray(delFilePathList);
            messageProducer.sendMessage(MqConstant.FILE_EXCHANGE_NAME, MqConstant.DEL_FILE_ROUTING_KEY, jsonArray.toString());
        }
        int index = 1;
        for (VideoFilePost videoFilePost : videoFilePosts) {
            videoFilePost.setFileIndex(index++);
            videoFilePost.setVideoId(videoPost.getId());
            videoFilePost.setUserId(videoPost.getUserId());
            if (videoFilePost.getFileId() == null) {
                videoFilePost.setFileId(RandomUtil.randomString(CommonConstant.LENGTH_20));
                videoFilePost.setUpdateType(VideoFileTypeEnum.UPDATE.getValue());
                videoFilePost.setTransferResult(VideoFileTransferResultEnum.TRANSFER.getValue());
            }
        }
        videoFilePostService.saveOrUpdateBatch(videoFilePosts);
        if (!addFileList.isEmpty()) {
            for (VideoFilePost item : addFileList) {
                item.setUserId(videoPost.getUserId());
                item.setVideoId(videoPost.getId());
            }
            // 发送视频转码消息到消息队列
            JSONArray jsonArray = JSONUtil.parseArray(addFileList);
            messageProducer.sendMessage(MqConstant.FILE_EXCHANGE_NAME, MqConstant.TRANSFER_VIDEO_ROOTING_KEY, jsonArray.toString());
        }
    }

    @Override
    public void transferVideoFile(List<VideoFilePost> VideoFilePost) {

    }

    private Boolean changeVideo(VideoPost videoPost) {
        VideoPost dbPost = this.getById(videoPost.getId());
        // 标题、封面、标签、简介
        return !videoPost.getName().equals(dbPost.getName())
                || !videoPost.getCover().equals(dbPost.getCover())
                || !videoPost.getTags().equals(dbPost.getTags())
                || !videoPost.getIntroduction().equals(dbPost.getIntroduction());
    }
}




