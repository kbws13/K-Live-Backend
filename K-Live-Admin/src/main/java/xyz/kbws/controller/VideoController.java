package xyz.kbws.controller;

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
import xyz.kbws.constant.UserConstant;
import xyz.kbws.mapper.VideoPostMapper;
import xyz.kbws.model.dto.videoPost.VideoPostAuditRequest;
import xyz.kbws.model.dto.videoPost.VideoPostQueryRequest;
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
 * @date 2024/11/30
 * @description: 视频接口
 */
@Api(tags = "视频接口")
@RestController
@RequestMapping("/video")
public class VideoController {

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

    @ApiOperation(value = "查询稿件接口")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/loadVideoPost")
    public BaseResponse<Page<VideoPostVO>> loadVideoPost(@RequestBody VideoPostQueryRequest videoPostQueryRequest,
                                                         HttpServletRequest request) {
        String token = request.getHeader("token");
        UserVO userVO = redisComponent.getUserVO(token);
        List<VideoPostVO> record = videoPostMapper.loadVideoPost(videoPostQueryRequest, userVO.getId());
        Page<VideoPostVO> res = new Page<>();
        res.setRecords(record);
        res.setCurrent(videoPostQueryRequest.getCurrent());
        res.setSize(videoPostQueryRequest.getPageSize());
        res.setSize(record.size());
        return ResultUtils.success(res);
    }

    @ApiOperation(value = "视频审核接口")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/auditVideoPost")
    public void auditVideoPost(@RequestBody VideoPostAuditRequest videoPostAuditRequest) {

    }
}
