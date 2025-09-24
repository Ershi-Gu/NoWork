package com.ershi.chat.service.cache;

import com.ershi.common.constants.RedisKey;
import com.ershi.common.utils.RedisUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 消息投递表（Redis实现），用作消息投递后的接收方ack确认记录
 *
 * @author Ershi-Gu.
 * @since 2025-09-22
 */
@Slf4j
@Service
public class MsgAckCache {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 添加接收方未确认消息
     *
     * @param memberUidList
     * @param msgId
     */
    public void addUnAckMsg(List<Long> memberUidList, Long msgId) {
        long ackDeadline = getAckDeadline();
        stringRedisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            for (Long uid : memberUidList) {
                connection.zAdd(
                        stringRedisTemplate.getStringSerializer().serialize(getKey(uid)),
                        ackDeadline,
                        stringRedisTemplate.getStringSerializer().serialize(msgId.toString())
                );
            }
            return null;
        });

        log.info("=======> 消息:{} 已推送，等待客户端ack", msgId);
    }

    /**
     * 移除用户未确认的消息
     *
     * @param uid   用户ID
     * @param msgId 消息ID
     */
    public void removeUnAckMsg(Long uid, Long msgId) {
        RedisUtils.zRemove(getKey(uid), msgId);
    }

    /**
     * 获取超时消息
     */
    public Set<Long> getExpiredUnAckMsg() {
        // 获取当前时间一分钟（ack过期时间）前的超时消息，避免全量扫描
        Set<Long> expiredMsgIds = new HashSet<>();
        long cutoff = System.currentTimeMillis() - 60_000;

        // 遍历所有 unack:uid_* key
        Set<String> keys = stringRedisTemplate.keys("unack:uid_*");
        if (keys.isEmpty()) {
            return expiredMsgIds;
        }

        for (String key : keys) {
            ZSetOperations<String, String> zSetOps = stringRedisTemplate.opsForZSet();
            // 获取 score <= cutoff 的 msgId
            Set<String> expired = zSetOps.rangeByScore(key, 0, cutoff);
            if (expired != null && !expired.isEmpty()) {
                expiredMsgIds.addAll(expired.stream().map(Long::parseLong).collect(Collectors.toSet()));
            }
        }

        return expiredMsgIds;
    }

    /**
     * 获取用户未确认消息的key
     *
     * @param uid
     * @return {@link String }
     */
    private String getKey(Long uid) {
        return RedisKey.getKey(RedisKey.MSG_USER_UN_ACK_KEY, uid);
    }

    /**
     * 生成ack一分钟待确认时间戳
     *
     * @return 毫秒级时间戳
     */
    public static long getAckDeadline() {
        long now = System.currentTimeMillis();
        return now + 60L * 1000L;
    }
}
