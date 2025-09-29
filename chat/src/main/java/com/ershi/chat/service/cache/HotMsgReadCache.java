package com.ershi.chat.service.cache;

import com.ershi.chat.domain.dto.HotC10nReadRecord;
import com.ershi.common.constants.RedisKey;
import com.ershi.common.utils.RedisUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
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
    // todo 热点会话用户已读待开发
    public void userReadMsg(Long uid, Long roomId, Long msgId) {
        RedisUtils.hmset(getKey(uid), Map.of(String.valueOf(roomId), msgId));
    }

    /**
     * 查询用户热点会话已读消息记录
     *
     * @param uid
     * @return {@link Map }<{@link Long }, {@link Long }>
     */
    public List<HotC10nReadRecord> getUserReadMsg(Long uid) {
        Map<Object, Object> res = RedisUtils.hmget(getKey(uid));
        if (res.isEmpty()) {
            return Collections.emptyList();
        }

        return res.entrySet().stream()
                .map(e -> new HotC10nReadRecord(
                        Long.valueOf(e.getKey().toString()),
                        Long.valueOf(e.getValue().toString())
                ))
                .toList();
    }

    public String getKey(Long uid) {
        return RedisKey.getKey(RedisKey.USER_READ_HOT_MSG_KEY, uid);
    }
}
