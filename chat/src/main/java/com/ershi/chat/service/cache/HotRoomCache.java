package com.ershi.chat.service.cache;

import com.ershi.common.constants.RedisKey;
import com.ershi.common.utils.RedisUtils;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 热点会话Redis缓存
 *
 * @author Ershi
 * @date 2025/02/05
 */
@Component
public class HotRoomCache {

    /**
     * 降序获取热点会话lastMsgId在min和max之间的数据，包含边界
     *
     * @param hotMaxMsgId
     * @return {@link Set }<{@link ZSetOperations.TypedTuple }<{@link String }>> 返回最新的会话在前
     */
    public Set<ZSetOperations.TypedTuple<String>> getHotConversionPage(Double hotMaxMsgId, Long pageSize) {
        return RedisUtils.zReverseRangeByScoreWithScores(RedisKey.getKey(RedisKey.HOT_ROOM_LAST_MSG_KEY),
                hotMaxMsgId, pageSize);
    }

    /**
     * 更新指定热门群会话在记录表中的最新消息id-用于热门会话排序
     *
     * @param roomId
     * @param lastMsgId
     */
    public void refreshLastMsgId(Long roomId, Long lastMsgId) {
        RedisUtils.zAdd(RedisKey.getKey(RedisKey.HOT_ROOM_LAST_MSG_KEY), roomId, lastMsgId);
    }
}
