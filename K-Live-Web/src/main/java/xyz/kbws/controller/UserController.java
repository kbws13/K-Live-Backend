package xyz.kbws.controller;

import com.wf.captcha.ArithmeticCaptcha;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.kbws.common.BaseResponse;
import xyz.kbws.common.ResultUtils;
import xyz.kbws.model.entity.User;
import xyz.kbws.model.vo.CheckCodeVO;
import xyz.kbws.redis.RedisComponent;
import xyz.kbws.service.UserService;

import javax.annotation.Resource;
import java.sql.ResultSet;

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
}
