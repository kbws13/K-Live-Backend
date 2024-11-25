package xyz.kbws.controller;

import cn.hutool.core.util.StrUtil;
import com.wf.captcha.ArithmeticCaptcha;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import xyz.kbws.common.BaseResponse;
import xyz.kbws.common.ErrorCode;
import xyz.kbws.common.ResultUtils;
import xyz.kbws.constant.RedisConstant;
import xyz.kbws.exception.BusinessException;
import xyz.kbws.exception.ThrowUtils;
import xyz.kbws.manager.RedissonLimiter;
import xyz.kbws.model.dto.user.UserLoginRequest;
import xyz.kbws.model.dto.user.UserRegisterRequest;
import xyz.kbws.model.vo.CheckCodeVO;
import xyz.kbws.model.vo.UserVO;
import xyz.kbws.redis.RedisComponent;
import xyz.kbws.service.UserService;
import xyz.kbws.utils.JwtUtil;
import xyz.kbws.utils.NetUtil;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author kbws
 * @date 2024/11/24
 * @description:
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private RedisComponent redisComponent;

    @Resource
    private RedissonLimiter redissonLimiter;

    @GetMapping("/checkCode")
    public BaseResponse<CheckCodeVO> checkCode() {
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(100, 42);
        String code = captcha.text();
        String checkCodeKey = redisComponent.saveCheckCode(code);
        String checkCodeBase64 = captcha.toBase64();
        CheckCodeVO checkCodeVO = new CheckCodeVO();
        checkCodeVO.setCheckCode(checkCodeBase64);
        checkCodeVO.setCheckCodeKey(checkCodeKey);

        return ResultUtils.success(checkCodeVO);
    }

    @PostMapping("/register")
    public BaseResponse<Boolean> register(@RequestBody UserRegisterRequest userRegisterRequest) {
        try {
            ThrowUtils.throwIf(userRegisterRequest == null, ErrorCode.PARAMS_ERROR);
            if (!userRegisterRequest.getCheckCode().equalsIgnoreCase(redisComponent.getCheckCode(userRegisterRequest.getCheckCodeKey()))) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "验证码错误");
            }
            Boolean register = userService.register(userRegisterRequest);
            return ResultUtils.success(register);
        }finally {
            redisComponent.cleanCheckCode(userRegisterRequest.getCheckCodeKey());
        }
    }

    @PostMapping("/login")
    public BaseResponse<UserVO> login(@RequestBody UserLoginRequest userLoginRequest) {
        ThrowUtils.throwIf(userLoginRequest == null, ErrorCode.PARAMS_ERROR);
        String ip = NetUtil.getIpAddress();
        UserVO userVO = userService.login(userLoginRequest, ip);
        return ResultUtils.success(userVO);
    }

    @GetMapping("/autoLogin")
    public BaseResponse<UserVO> autoLogin(HttpServletRequest request) {
        String token = request.getHeader("token");
        UserVO userVO = redisComponent.getUserVO(token);
        if (userVO == null) {
            return ResultUtils.success(null);
        }
        if (userVO.getExpireAt() - System.currentTimeMillis() < RedisConstant.TIME_1DAY) {
            redisComponent.saveUserVO(userVO);
        }
        return ResultUtils.success(userVO);
    }

    @GetMapping("/logout")
    public BaseResponse<String> logout(HttpServletRequest request) {
        String token = request.getHeader("token");
        if (StrUtil.isBlank(token)) {
            return ResultUtils.success(null);
        }
        redisComponent.cleanToken(token);
        return ResultUtils.success("退出登录成功");
    }
}
