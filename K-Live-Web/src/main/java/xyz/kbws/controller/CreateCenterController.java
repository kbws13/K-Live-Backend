package xyz.kbws.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.kbws.annotation.AuthCheck;
import xyz.kbws.common.BaseResponse;
import xyz.kbws.common.ResultUtils;
import xyz.kbws.mapper.VideoPostMapper;
import xyz.kbws.model.dto.videoPost.VideoPostAddRequest;
import xyz.kbws.model.dto.videoPost.VideoPostQueryRequest;
import xyz.kbws.model.dto.videoPost.VideoPostUpdateRequest;
import xyz.kbws.model.entity.Video;
import xyz.kbws.model.entity.VideoFilePost;
import xyz.kbws.model.entity.VideoPost;
import xyz.kbws.model.enums.VideoStatusEnum;
import xyz.kbws.model.vo.UserVO;
import xyz.kbws.model.vo.VideoPostVO;
import xyz.kbws.redis.RedisComponent;
import xyz.kbws.service.VideoFilePostService;
import xyz.kbws.service.VideoPostService;
import xyz.kbws.service.VideoService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author kbws
 * @date 2024/11/28
 * @description:
 */
@Api(tags = "创作中心接口")
@RestController
@RequestMapping("/createCenter")
public class CreateCenterController {

    @Resource
    private VideoPostService videoPostService;

    @Resource
    private VideoFilePostService videoFilePostService;

    @Resource
    private VideoService videoService;

    @Resource
    private VideoPostMapper videoPostMapper;

    @Resource
    private RedisComponent redisComponent;

    @ApiOperation(value = "发布视频")
    @AuthCheck
    @PostMapping("/addPostVideo")
    public void addPostVideo(@RequestBody VideoPostAddRequest videoPostAddRequest, HttpServletRequest request) {
        String token = request.getHeader("token");
        UserVO userVO = redisComponent.getUserVO(token);
        List<VideoFilePost> videoFilePosts = videoPostAddRequest.getVideoFilePosts();
        VideoPost videoPost = new VideoPost();
        BeanUtil.copyProperties(videoPostAddRequest, videoPost);
        videoPost.setUserId(userVO.getId());
        videoPostService.addVideoPost(videoPost, videoFilePosts);
    }

    @ApiOperation(value = "修改视频")
    @AuthCheck
    @PostMapping("/updatePostVideo")
    public void updatePostVide(@RequestBody VideoPostUpdateRequest videoPostUpdateRequest, HttpServletRequest request) {
        String token = request.getHeader("token");
        UserVO userVO = redisComponent.getUserVO(token);
        List<VideoFilePost> videoFilePosts = videoPostUpdateRequest.getVideoFilePosts();
        VideoPost videoPost = new VideoPost();
        BeanUtil.copyProperties(videoPostUpdateRequest, videoPost);
        videoPost.setUserId(userVO.getId());
        videoPostService.updateVideoPost(videoPost, videoFilePosts);
    }

    @ApiOperation(value = "查询稿件接口")
    @AuthCheck
    @PostMapping("/loadVideoPost")
    public BaseResponse<Page<VideoPostVO>> loadVideoPost(@RequestBody VideoPostQueryRequest videoPostQueryRequest,
                                                       HttpServletRequest request) {
        String token = request.getHeader("token");
        UserVO userVO = redisComponent.getUserVO(token);
        Integer status = videoPostQueryRequest.getStatus();
        if (status != null) {
            if (status == -1) {
                Integer[] array = {VideoStatusEnum.STATUS3.getValue(), VideoStatusEnum.STATUS4.getValue()};
                List<Integer> list = new ArrayList<>(Arrays.asList(array));
                videoPostQueryRequest.setExcludeStatus(list);
            }
        }
        List<VideoPostVO> record = videoPostMapper.loadVideoPost(videoPostQueryRequest, userVO.getId());
        Page<VideoPostVO> res = new Page<>();
        res.setRecords(record);
        res.setCurrent(videoPostQueryRequest.getCurrent());
        res.setSize(videoPostQueryRequest.getPageSize());
        res.setSize(record.size());
        return ResultUtils.success(res);
    }

}
