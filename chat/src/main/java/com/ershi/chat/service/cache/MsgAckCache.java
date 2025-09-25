package com.ershi.chat.service.cache;

import com.ershi.common.constants.RedisKey;
import com.ershi.common.utils.RedisUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.*;
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
     * 客户按接收消息ack超时时间（ms） - 10s -10000ms
     */
    public static final Long ACK_TIME_MILLIS = 10L * 1000L;

    /**
     * 添加接收方未确认消息
     *
     * @param memberUidList
     * @param msgId
     */
    public void addUnAckMsg(List<Long> memberUidList, Long msgId) {
        if (memberUidList.isEmpty()) {
            return;
        }

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
     * @param uid    用户ID
     * @param msgIds
     */
    public void removeUnAckMsg(Long uid, List<Long> msgIds) {
        RedisUtils.zRemove(getKey(uid), msgIds.toArray());
    }

    /**
     * 获取每个 uid 对应的超时消息
     */
    public Map<Long, Set<Long>> getExpiredUnAckMsg() {
        Map<Long, Set<Long>> result = new HashMap<>();

        // 获取当前时间前10秒的时间戳，避免全量扫描，更古老的消息不作处理
        long cutoff = System.currentTimeMillis() - ACK_TIME_MILLIS;

        // key匹配前缀
        String prefixKey = RedisKey.BASE_KEY + "unack:uid_";

        // 遍历所有 unack:uid_* key
        Set<String> keys = new HashSet<>(RedisUtils.scan(prefixKey + "*"));
        if (keys.isEmpty()) {
            return result;
        }

        ZSetOperations<String, String> zSetOps = stringRedisTemplate.opsForZSet();

        for (String key : keys) {
            // 从 key 提取 uid
            String uidStr = key.substring((prefixKey).length());
            Long uid = Long.parseLong(uidStr);

            // 获取扫描范围内超时的msgId
            Set<String> expired = zSetOps.rangeByScore(key, cutoff, System.currentTimeMillis());
            if (expired != null && !expired.isEmpty()) {
                Set<Long> expiredMsgIds = expired.stream()
                        .map(Long::parseLong)
                        .collect(Collectors.toSet());

                result.put(uid, expiredMsgIds);
            }
        }

        return result;
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
     * 生成消息ack超时时间戳（10秒未回复ack则表示超时）
     *
     * @return 毫秒级时间戳
     */
    public static long getAckDeadline() {
        long now = System.currentTimeMillis();
        return now + ACK_TIME_MILLIS;
    }
}
