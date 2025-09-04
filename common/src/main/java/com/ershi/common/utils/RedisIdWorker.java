package com.ershi.common.utils;

import com.ershi.common.constants.RedisKey;
import com.ershi.common.exception.SystemCommonErrorEnum;
import org.springframework.batch.core.step.tasklet.SystemCommandException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

/**
 * 通过Redis实现的全局唯一id，通常用于生成唯一请求 d
 *
 * @author Ershi-Gu.
 * @since 2025-09-02
 */
@Component
public class RedisIdWorker {

    /**
     * 开始时间戳
     */
    private static final long BEGIN_TIMESTAMP = 1640995200L;
    /**
     * 序列号的位数
     */
    private static final int COUNT_BITS = 32;
    /**
     * 请求id过期时间设置为一天（每天重置）
     */
    private static final int REQUEST_ID_EXPIRE_TIME = 24 * 60 * 60;

    public String nextId() {
        // 1.生成时间戳
        LocalDateTime now = LocalDateTime.now();
        long nowSecond = now.toEpochSecond(ZoneOffset.UTC);
        long timestamp = nowSecond - BEGIN_TIMESTAMP;

        // 2.生成序列号
        // 2.1.获取当前日期，精确到天
        String date = now.format(DateTimeFormatter.ofPattern("yyyy:MM:dd"));
        // 2.2.自增长
        Long count = RedisUtils.inc(RedisKey.getKey(RedisKey.REQUEST_ID_KEY, date), REQUEST_ID_EXPIRE_TIME, TimeUnit.SECONDS);

        // 3.拼接并返回
        AssertUtil.nonNull(count, SystemCommonErrorEnum.REDIS_ERROR);
        long id = (timestamp << COUNT_BITS) | (count & 0xFFFFFFFFL);
        return String.valueOf(id);
    }

}