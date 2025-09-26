package com.ershi.chat.service.cache;

import com.ershi.common.constants.RedisKey;
import com.ershi.common.utils.RedisUtils;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * 热点会话消息用已读记录，记录在redis中，用于热点绘画查询未读数时使用
 *
 * @author Ershi-Gu.
 * @since 2025-09-26
 */
@Service
public class HotMsgReadCache {

    /**
     * 记录热点会话用户已读消息<br>
     * - key: uid <br>
     * - filedId: roomId <br>
     * - value: msgId
     *
     * @param roomId
     * @param msgId
     */
    public void userReadMsg(Long uid, Long roomId, Long msgId) {
        RedisUtils.hmset(getKey(uid), Map.of(String.valueOf(roomId), msgId));
    }

    /**
     * 查询用户热点会话已读消息记录
     *
     * @return {@link Map }<{@link Long }, {@link Long }> roomId -> msgId
     */
    public Map<Long, Long> getUserReadMsg(Long uid) {
        Map<Object, Object> res = RedisUtils.hmget(getKey(uid));
        return res.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> Long.valueOf(e.getKey().toString()),
                        e -> Long.valueOf(e.getValue().toString())
                ));
    }

    public String getKey(Long uid) {
        return RedisKey.getKey(RedisKey.USER_READ_HOT_MSG_KEY, uid);
    }
}
