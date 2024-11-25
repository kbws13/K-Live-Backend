package xyz.kbws.manager;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author kbws
 * @date 2024/11/25
 * @description: 限流服务
 */
@Slf4j
@Component
public class RedissonLimiter {

    @Resource
    private RedissonClient redissonClient;

    public boolean deRateLimit(String key) {
        // 创建一个限流器
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(key);
        // 每秒最多访问两次
        // type: 限流类型 rate: 限流速率 rateInterval: 限流时间间隔 unit: 时间间隔单位
        boolean trySetRate = rateLimiter.trySetRate(RateType.OVERALL, 2, 1, RateIntervalUnit.SECONDS);
        if (trySetRate) {
            log.info("init rate = {}, interval = {}", rateLimiter.getConfig().getRate(), rateLimiter.getConfig().getRateInterval());
        }
        // 每当一个请求过来，请求一个令牌
        return rateLimiter.tryAcquire(1);
    }
}
