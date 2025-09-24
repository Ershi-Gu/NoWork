package com.ershi.chat.service.cache;

import com.ershi.chat.domain.message.MessageEntity;
import com.ershi.common.constants.RedisKey;
import com.ershi.common.utils.RedisUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 消息简表（redis实现），用于过期未ack的消息重试，减轻db压力
 *
 * @author Ershi-Gu.
 * @since 2025-09-24
 */
@Service
public class MessageCache {

    /**
     * 消息简表过期时间 - 1day
     */
    public static final long MSG_INFO_EXPIRE_DAY = 1L;

    /**
     * 将消息加入消息简表
     *
     * @param messageEntity
     */
    public void addMsgToCache(MessageEntity messageEntity) {
        RedisUtils.set(getKey(messageEntity.getId()), messageEntity, MSG_INFO_EXPIRE_DAY, TimeUnit.DAYS);
    }

    public List<MessageEntity> batchGetMsgFromCache(List<Long> msgId) {
        List<String> keys = msgId.stream().map(this::getKey).toList();
        return RedisUtils.mget(keys, MessageEntity.class);
    }

    /**
     * 删除缓存
     *
     * @param msgId
     */
    public void remove(Long msgId) {
        RedisUtils.del(getKey(msgId));
    }

    /**
     * 获取消息简表key
     *
     * @param msgId
     * @return {@link String }
     */
    private String getKey(Long msgId) {
        return RedisKey.getKey(RedisKey.MSG_INFO_KEY, msgId);
    }
}
