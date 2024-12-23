package xyz.kbws.controller;

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
import xyz.kbws.constant.UserConstant;
import xyz.kbws.model.dto.user.UserChangeStatusRequest;
import xyz.kbws.model.dto.user.UserLoadRequest;
import xyz.kbws.model.entity.User;
import xyz.kbws.service.UserService;

import javax.annotation.Resource;

/**
 * @author kbws
 * @date 2024/12/18
 * @description: 用户管理接口
 */
@Api(tags = "用户管理接口")
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @ApiOperation(value = "查询用户")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/loadUser")
    public BaseResponse<Page<User>> loadUser(@RequestBody UserLoadRequest userLoadRequest) {
        long current = userLoadRequest.getCurrent();
        long pageSize = userLoadRequest.getPageSize();
        userLoadRequest.setSortField("createTime");
        QueryWrapper<User> queryWrapper = userService.getQueryWrapper(userLoadRequest);
        Page<User> page = userService.page(new Page<>(current, pageSize), queryWrapper);
        return ResultUtils.success(page);
    }

    @ApiOperation(value = "修改用户状态")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/changeStatus")
    public BaseResponse<Boolean> changeStatus(@RequestBody UserChangeStatusRequest userChangeStatusRequest) {
        Boolean res = userService.changeStatus(userChangeStatusRequest);
        return ResultUtils.success(res);
    }
}
