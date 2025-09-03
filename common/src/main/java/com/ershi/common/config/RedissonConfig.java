package com.ershi.common.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redisson 配置类
 *
 * @author Ershi
 * @date 2024/11/29
 */
@Configuration
public class RedissonConfig {

    @Bean
    public RedissonClient redissonClient(RedisProperties redisProperties) {
        String host = redisProperties.getHost();
        int port = redisProperties.getPort();
        String password = redisProperties.getPassword();

        Config config = new Config();
        config.useSingleServer().setAddress(String.format("redis://%s:%d", host, port))
            .setDatabase(redisProperties.getDatabase())
            .setPassword(password != null && !password.isEmpty() ? password : null)
            .setConnectionPoolSize(10)
            .setConnectionMinimumIdleSize(2);
        return Redisson.create(config);
    }
}
