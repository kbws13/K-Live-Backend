package xyz.kbws.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import xyz.kbws.common.BaseResponse;
import xyz.kbws.common.ErrorCode;
import xyz.kbws.common.ResultUtils;
import xyz.kbws.constant.CommonConstant;
import xyz.kbws.exception.BusinessException;
import xyz.kbws.mapper.VideoMapper;
import xyz.kbws.model.dto.video.VideoQueryRequest;
import xyz.kbws.model.dto.video.VideoReportRequest;
import xyz.kbws.model.entity.Video;
import xyz.kbws.model.entity.VideoFile;
import xyz.kbws.model.enums.VideoRecommendTypeEnum;
import xyz.kbws.model.vo.VideoInfoResultVO;
import xyz.kbws.model.vo.VideoVO;
import xyz.kbws.redis.RedisComponent;
import xyz.kbws.service.VideoFileService;
import xyz.kbws.service.VideoService;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

/**
 * @author kbws
 * @date 2024/12/5
 * @description: 视频接口
 */
@Api(tags = "视频接口")
@RestController
@RequestMapping("/video")
public class VideoController {

    @Resource
    private VideoService videoService;

    @Resource
    private VideoFileService videoFileService;

    @Resource
    private VideoMapper videoMapper;

    @Resource
    private RedisComponent redisComponent;

    @ApiOperation(value = "获取推荐视频列表")
    @GetMapping("/loadRecommendVideo")
    public BaseResponse<List<VideoVO>> loadRecommendVideo() {
        VideoQueryRequest videoQueryRequest = new VideoQueryRequest();
        videoQueryRequest.setQueryUserInfo(true);
        videoQueryRequest.setSortField("createTime");
        videoQueryRequest.setSortOrder(CommonConstant.SORT_ORDER_DESC);
        videoQueryRequest.setRecommendType(VideoRecommendTypeEnum.RECOMMEND.getValue());
        List<VideoVO> list = videoMapper.queryList(videoQueryRequest);
        return ResultUtils.success(list);
    }

    @ApiOperation(value = "获取非推荐视频列表")
    @PostMapping("/loadVideoList")
    public BaseResponse<Page<VideoVO>> loadVideoList(@RequestBody VideoQueryRequest videoQueryRequest) {
        long current = videoQueryRequest.getCurrent();
        long pageSize = videoQueryRequest.getPageSize();
        videoQueryRequest.setRecommendType(VideoRecommendTypeEnum.NO_RECOMMEND.getValue());
        videoQueryRequest.setQueryUserInfo(true);
        List<VideoVO> record = videoMapper.queryList(videoQueryRequest);
        Page<VideoVO> page = new Page<>();
        page.setRecords(record);
        page.setSize(record.size());
        page.setCurrent(current);
        page.setSize(pageSize);
        return ResultUtils.success(page);
    }

    @ApiOperation(value = "获取视频信息")
    @GetMapping("/getVideoInfo")
    public BaseResponse<VideoInfoResultVO> getVideoInfo(@NotEmpty(message = "视频 id 不能为空") String videoId) {
        Video video = videoService.getById(videoId);
        if (video == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "该视频不存在");
        }
        // TODO 获取用户点赞、投币、收藏
        List<Integer> list = new ArrayList<>();
        VideoInfoResultVO videoInfoResultVO = new VideoInfoResultVO();
        videoInfoResultVO.setVideo(video);
        videoInfoResultVO.setUserActionList(list);
        return ResultUtils.success(videoInfoResultVO);
    }

    @ApiOperation(value = "获取视频分集列表")
    @GetMapping("/loadVideoPList")
    public BaseResponse<List<VideoFile>> loadVideoPList(@NotEmpty(message = "视频 id 不能为空") String videoId) {
        QueryWrapper<VideoFile> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("videoId", videoId);
        queryWrapper.orderByAsc("fileIndex");
        List<VideoFile> list = videoFileService.list(queryWrapper);
        return ResultUtils.success(list);
    }

    @ApiOperation(value = "上报在线观看人数")
    @PostMapping("/reportVideoPlyaOnline")
    public BaseResponse<Integer> repostVideoPlayOnline(@RequestBody VideoReportRequest videoReportRequest) {
        return ResultUtils.success(10);
    }
}
