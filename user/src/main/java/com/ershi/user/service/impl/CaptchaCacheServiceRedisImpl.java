package com.ershi.user.service.impl;

import com.anji.captcha.service.CaptchaCacheService;
import com.ershi.common.constants.RedisKey;
import com.ershi.common.utils.RedisUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.Collections;

/**
 * 对于分布式部署的应用，我们建议应用自己实现CaptchaCacheService，
 * 比如用Redis，参考service/ spring-boot代码示例。 如果应用是单点的，也没有使用redis，那默认使用内存。
 * 内存缓存只适合单节点部署的应用，否则验证码生产与验证在节点之间信息不同步，导致失败。
 * ☆☆☆ SPI： 在resources目录新建META-INF. services文件夹(两层)，参考当前服务resources。
 *
 * @author Ershi-Gu.
 * @since 2025-09-08
 */
public class CaptchaCacheServiceRedisImpl implements CaptchaCacheService {

    private static final String LUA_SCRIPT = "local key = KEYS[1] " +
            "local incrementValue = tonumber(ARGV[1]) " +
            "if redis.call('EXISTS', key) == 1 then " +
            "    return redis.call('INCRBY', key, incrementValue) " +
            "else " +
            "    return incrementValue " +
            "end";

    private StringRedisTemplate stringRedisTemplate;

    public void setStringRedisTemplate(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 缓存类型-local/redis/memcache/..
     * 通过java SPI机制，接入方可自定义实现类
     *
     * @return {@link String }
     */
    @Override
    public String type() {
        return "redis";
    }

    @Override
    public void set(String key, String value, long expiresInSeconds) {
        RedisUtils.set(RedisKey.getKey(RedisKey.ANJI_CAPTCHA_KEY, key), value, expiresInSeconds);
    }

    @Override
    public boolean exists(String key) {
        return RedisUtils.hasKey(RedisKey.getKey(RedisKey.ANJI_CAPTCHA_KEY, key));
    }

    @Override
    public void delete(String key) {
        RedisUtils.del(RedisKey.getKey(RedisKey.ANJI_CAPTCHA_KEY, key));
    }

    @Override
    public String get(String key) {
        return RedisUtils.get(RedisKey.getKey(RedisKey.ANJI_CAPTCHA_KEY, key), String.class);
    }

    @Override
    public Long increment(String key, long val) {
        // 执行 Lua 脚本
        RedisScript<Long> script = new DefaultRedisScript<>(LUA_SCRIPT, Long.class);
        // 执行 Lua 脚本
        return stringRedisTemplate.execute(
                script,
                Collections.singletonList(RedisKey.getKey(RedisKey.ANJI_CAPTCHA_KEY, key)),
                String.valueOf(val)
        );
    }

    @Override
    public void setExpire(String key, long expiresInSeconds) {
        RedisUtils.expire(key, expiresInSeconds);
    }
}
