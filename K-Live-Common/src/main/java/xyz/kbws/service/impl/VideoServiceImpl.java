package xyz.kbws.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.kbws.common.ErrorCode;
import xyz.kbws.config.SystemSetting;
import xyz.kbws.es.EsComponent;
import xyz.kbws.exception.BusinessException;
import xyz.kbws.mapper.UserMapper;
import xyz.kbws.mapper.VideoMapper;
import xyz.kbws.model.dto.video.VideoQueryRequest;
import xyz.kbws.model.entity.Video;
import xyz.kbws.model.entity.VideoFile;
import xyz.kbws.model.entity.VideoPost;
import xyz.kbws.model.enums.UserActionTypeEnum;
import xyz.kbws.redis.RedisComponent;
import xyz.kbws.service.VideoPostService;
import xyz.kbws.service.VideoService;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author fangyuan
 * @description 针对表【video(视频信息表)】的数据库操作Service实现
 * @createDate 2024-11-28 20:36:09
 */
@Service
public class VideoServiceImpl extends ServiceImpl<VideoMapper, Video>
        implements VideoService {

    @Resource
    private VideoPostService videoPostService;

    @Resource
    private VideoMapper videoMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private RedisComponent redisComponent;

    @Resource
    private EsComponent esComponent;

    private ExecutorService executorService = Executors.newFixedThreadPool(10);

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteVideo(String videoId, String userId) {
        Video video = this.getById(videoId);
        if (video == null || userId != null && !video.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        this.removeById(videoId);
        videoPostService.removeById(videoId);
        // 减硬币
        SystemSetting systemSetting = redisComponent.getSystemSetting();
        userMapper.updateCoinCount(video.getUserId(), -systemSetting.getPostVideoCoinCount());
        // 删除 ES 信息
        esComponent.deleteDoc(videoId);
        executorService.execute(() -> {
            // TODO 删除分 p
            // TODO 删除弹幕
            // TODO 删除评论
        });
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void changeInteraction(String videoId, String userId, String interaction) {
        Video video = new Video();
        video.setInteraction(interaction);
        QueryWrapper<Video> videoQueryWrapper = new QueryWrapper<>();
        videoQueryWrapper.eq("id", videoId)
                .eq("userId", userId);
        this.update(video, videoQueryWrapper);

        VideoPost videoPost = new VideoPost();
        videoPost.setInteraction(interaction);
        QueryWrapper<VideoPost> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", videoId)
                .eq("userId", userId);
        videoPostService.update(videoPost, queryWrapper);
    }

    @Override
    public List<Video> selectList(VideoQueryRequest videoQueryRequest) {
        return videoMapper.queryList(videoQueryRequest);
    }

    @Override
    public void addPlayCount(String videoId) {
        videoMapper.updateCountInfo(videoId, UserActionTypeEnum.VIDEO_PLAY.getField(), 1);
    }
}




