package xyz.kbws.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import xyz.kbws.annotation.AuthCheck;
import xyz.kbws.common.BaseResponse;
import xyz.kbws.common.ResultUtils;
import xyz.kbws.constant.UserConstant;
import xyz.kbws.model.dto.user.UserUpdateRequest;
import xyz.kbws.model.entity.Focus;
import xyz.kbws.model.entity.User;
import xyz.kbws.model.query.FocusQuery;
import xyz.kbws.model.vo.UserVO;
import xyz.kbws.redis.RedisComponent;
import xyz.kbws.service.ActionService;
import xyz.kbws.service.FocusService;
import xyz.kbws.service.UserService;
import xyz.kbws.service.VideoService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

/**
 * @author kbws
 * @date 2024/12/9
 * @description:
 */
@Api(tags = "主页接口")
@RestController
@RequestMapping("/home")
public class HomePageController {

    @Resource
    private UserService userService;

    @Resource
    private VideoService videoService;

    @Resource
    private FocusService focusService;

    @Resource
    private ActionService actionService;

    @Resource
    private RedisComponent redisComponent;

    @ApiOperation(value = "获取个人账号信息")
    @GetMapping("/getUserInfo")
    public BaseResponse<UserVO> getUserInfo(@NotEmpty String userId, HttpServletRequest request) {
        String token = request.getHeader("token");
        UserVO userVO = redisComponent.getUserVO(token);
        String currentUserId = userVO == null ? null : userVO.getId();
        UserVO userDetailInfo = userService.getUserDetailInfo(currentUserId, userId);
        return ResultUtils.success(userDetailInfo);
    }

    @ApiOperation(value = "更新个人信息")
    @AuthCheck
    @PostMapping("/updateUserInfo")
    public BaseResponse<Boolean> updateUserInfo(@RequestBody UserUpdateRequest userUpdateRequest, HttpServletRequest request) {
        String token = request.getHeader("token");
        UserVO userVO = redisComponent.getUserVO(token);
        User user = userService.getById(userVO.getId());
        BeanUtil.copyProperties(userUpdateRequest, user);
        boolean res = userService.updateUserInfo(user, userVO);
        return ResultUtils.success(res);
    }

    @ApiOperation(value = "更新主题")
    @AuthCheck
    @PostMapping("/updateTheme")
    public BaseResponse<Boolean> updateTheme(@Min(1) @Max(10) Integer theme, HttpServletRequest request) {
        String token = request.getHeader("token");
        UserVO userVO = redisComponent.getUserVO(token);
        User user = userService.getById(userVO.getId());
        user.setTheme(theme);
        boolean res = userService.updateById(user);
        return ResultUtils.success(res);
    }

    @ApiOperation(value = "关注")
    @AuthCheck
    @PostMapping("/focus")
    public BaseResponse<Boolean> focusUser(@NotEmpty String focusUserId, HttpServletRequest request) {
        String token = request.getHeader("token");
        UserVO userVO = redisComponent.getUserVO(token);
        boolean res = focusService.focusUser(userVO.getId(), focusUserId);
        return ResultUtils.success(res);
    }

    @ApiOperation(value = "取消关注")
    @AuthCheck
    @PostMapping("/ cancelFocus")
    public BaseResponse<Boolean> cancelFocusUser(@NotEmpty String focusUserId, HttpServletRequest request) {
        String token = request.getHeader("token");
        UserVO userVO = redisComponent.getUserVO(token);
        boolean res = focusService.cancelFocusUser(userVO.getId(), focusUserId);
        return ResultUtils.success(res);
    }

    @ApiOperation(value = "加载关注列表")
    @AuthCheck
    @GetMapping("/loadFocusList")
    public BaseResponse<Page<Focus>> loadFocusList(Integer pageNo, HttpServletRequest request) {
        String token = request.getHeader("token");
        UserVO userVO = redisComponent.getUserVO(token);
        FocusQuery focusQuery = new FocusQuery();
        focusQuery.setUserId(userVO.getId());
        focusQuery.setPageNo(pageNo);
        focusQuery.setQueryType(UserConstant.ZERO);
        focusQuery.setOrderBy("focusTime desc");
        return ResultUtils.success(null);
    }

}
