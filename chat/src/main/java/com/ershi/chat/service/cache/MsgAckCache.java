package com.ershi.chat.service.cache;

import com.ershi.common.constants.RedisKey;
import com.ershi.common.utils.RedisUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

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
        long expireTimestamp = generateExpireTimestamp();
        stringRedisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            for (Long uid : memberUidList) {
                connection.zAdd(
                        stringRedisTemplate.getStringSerializer().serialize(getKey(uid)),
                        expireTimestamp,
                        stringRedisTemplate.getStringSerializer().serialize(msgId.toString())
                );
            }
            return null;
        });

        log.info("=======> 消息推送unack标志已生成，等待客户端ack");
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
    public Set<String> getExpiredUnAckMsg(Long uid, long nowTimestamp) {
        return RedisUtils.zRange(getKey(uid), 0, nowTimestamp);
    }

    private String getKey(Long uid) {
        return RedisKey.getKey(RedisKey.MSG_USER_UN_ACK_KEY, uid);
    }

    /**
     * 生成ack一分钟待确认时间戳
     *
     * @return 毫秒级时间戳
     */
    public static long generateExpireTimestamp() {
        long now = System.currentTimeMillis();
        return now + 60L * 1000L;
    }
}
