package xyz.kbws.redis;

import cn.hutool.core.util.RandomUtil;
import org.springframework.stereotype.Component;
import xyz.kbws.constant.RedisConstant;

import javax.annotation.Resource;

/**
 * @author kbws
 * @date 2024/11/24
 * @description:
 */
@Component
public class RedisComponent {

    @Resource
    private RedisUtils<String> redisUtils;

    public String saveCheckCode(String code) {
        String checkCodeKey = RandomUtil.randomString(8);
        redisUtils.setEx(RedisConstant.CHECK_CODE + checkCodeKey, code, RedisConstant.TIME_1MIN);
        return checkCodeKey;
    }
}
