package xyz.kbws.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.kbws.annotation.AuthCheck;
import xyz.kbws.common.BaseResponse;
import xyz.kbws.common.ErrorCode;
import xyz.kbws.common.ResultUtils;
import xyz.kbws.constant.UserConstant;
import xyz.kbws.exception.BusinessException;
import xyz.kbws.exception.ThrowUtils;
import xyz.kbws.model.dto.danmu.DanmuLoadRequest;
import xyz.kbws.model.dto.danmu.DanmuPostRequest;
import xyz.kbws.model.entity.Danmu;
import xyz.kbws.model.entity.Video;
import xyz.kbws.model.vo.UserVO;
import xyz.kbws.redis.RedisComponent;
import xyz.kbws.service.DanmuService;
import xyz.kbws.service.VideoService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @author kbws
 * @date 2024/12/7
 * @description: 弹幕接口
 */
@Api(tags = "弹幕接口")
@RestController
@RequestMapping("/danmu")
public class DanmuController {

    @Resource
    private DanmuService danmuService;

    @Resource
    private VideoService videoService;

    @Resource
    private RedisComponent redisComponent;

    @ApiOperation(value = "发弹幕")
    @AuthCheck
    @PostMapping("/postDanmu")
    public void postDanmu(@RequestBody DanmuPostRequest danmuPostRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(danmuPostRequest == null, ErrorCode.PARAMS_ERROR);
        String token = request.getHeader("token");
        UserVO userVO = redisComponent.getUserVO(token);
        Danmu danmu = new Danmu();
        BeanUtil.copyProperties(danmuPostRequest, danmu);
        danmu.setUserId(userVO.getId());
        danmu.setPostTime(DateUtil.date());
        danmuService.saveDanmu(danmu);
    }

    @ApiOperation(value = "加载弹幕")
    @PostMapping("/loadDanmu")
    public BaseResponse<List<Danmu>> loadDanmu(@RequestBody DanmuLoadRequest danmuLoadRequest) {
        Video video = videoService.getById(danmuLoadRequest.getVideoId());
        if (video.getInteraction() != null && video.getInteraction().contains(UserConstant.ONE.toString())) {
            return ResultUtils.success(new ArrayList<>());
        }
        QueryWrapper<Danmu> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("fileId", danmuLoadRequest.getFileId());
        queryWrapper.orderByAsc("id");
        List<Danmu> list = danmuService.list(queryWrapper);
        return ResultUtils.success(list);
    }
}
