package xyz.kbws.controller;

import cn.hutool.core.bean.BeanUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.kbws.annotation.AuthCheck;
import xyz.kbws.common.BaseResponse;
import xyz.kbws.model.dto.videoPost.VideoPostAddRequest;
import xyz.kbws.model.entity.VideoFilePost;
import xyz.kbws.model.entity.VideoPost;
import xyz.kbws.model.vo.UserVO;
import xyz.kbws.redis.RedisComponent;
import xyz.kbws.service.VideoFilePostService;
import xyz.kbws.service.VideoPostService;
import xyz.kbws.service.VideoService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
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

}
