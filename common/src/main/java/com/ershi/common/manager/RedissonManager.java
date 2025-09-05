package com.ershi.common.manager;

import com.ershi.common.constants.RedisKey;
import com.ershi.common.exception.BusinessErrorEnum;
import com.ershi.common.exception.BusinessException;
import com.ershi.common.exception.SystemCommonErrorEnum;
import jakarta.annotation.Resource;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

/**
 * 封装 Redisson 相关功能，例如限流
 *
 * @author Ershi-Gu.
 * @since 2025-09-05
 */
@Component
public class RedissonManager {

    @Resource
    private RedissonClient redissonClient;

    /**
     * 全局限流，不同key单独限流
     *
     * @param key      区分不同的限流器
     * @param rate     允许的操作次数（发放的令牌数）
     * @param interval 限流周期时长
     * @param unit     时间单位
     */
    public void doOverallRateLimit(String key, Long rate, Long interval, RateIntervalUnit unit) {
        // 根据key获取限流器
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(RedisKey.getKey(RedisKey.REDISSON_RATE_LIMITER_KEY, key));

        rateLimiter.trySetRate(RateType.OVERALL, rate, interval, unit);

        // 每次请求消耗1个令牌
        boolean canProceed = rateLimiter.tryAcquire(1);

        if (!canProceed) {
            throw new BusinessException(SystemCommonErrorEnum.LOCK_LIMIT);
        }
    }

}
