package com.ershi.chat.service.cache;

import com.ershi.common.constants.RedisKey;
import com.ershi.common.utils.RedisUtils;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 热点会话Redis缓存
 *
 * @author Ershi
 * @date 2025/02/05
 */
@Component
public class HotRoomCache {

    /**
     * 降序获取热点会话lastMsgId在min和max之间的数据，包含边界。<br>
     *
     * @param hotMaxMsgId
     * @return {@link Set }<{@link ZSetOperations.TypedTuple }<{@link String }>> 返回最新的会话在前
     */
    public Set<ZSetOperations.TypedTuple<String>> getHotConversationPage(Double hotMaxMsgId, Long pageSize) {
        return RedisUtils.zReverseRangeByScoreWithScores(getKey(),
                hotMaxMsgId, pageSize);
    }

    /**
     * 获取指定热点会话最新消息id。
     *
     * @param roomIds
     * @return {@link Map }<{@link Long }, {@link Long }> roomId-lastMsgId
     */
    public Map<Long, Long> getHotConversationLastMsgIds(List<Long> roomIds) {
        if (roomIds == null || roomIds.isEmpty()) {
            return Collections.emptyMap();
        }

        // 批量获取 score
        List<Double> scores = RedisUtils.zMScore(getKey(), roomIds);

        // 构造 Map<roomId, lastMsgId>
        Map<Long, Long> result = new LinkedHashMap<>();
        for (int i = 0; i < roomIds.size(); i++) {
            Long roomId = roomIds.get(i);
            Double score = scores.get(i);
            // score 为 null 的说明该 roomId 不存在 ZSet，设置为 null
            result.put(roomId, score != null ? score.longValue() : null);
        }

        return result;
    }

    /**
     * 更新指定热门群会话在记录表中的最新消息id-用于热门会话排序
     *
     * @param roomId
     * @param lastMsgId
     */
    public void refreshLastMsgId(Long roomId, Long lastMsgId) {
        RedisUtils.zAdd(getKey(), roomId, lastMsgId);
    }

    private String getKey() {
        return RedisKey.getKey(RedisKey.HOT_ROOM_LAST_MSG_KEY);
    }
}
