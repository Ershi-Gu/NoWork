package com.ershi.chat.service.cache;

import com.ershi.chat.domain.dto.HotC10nLastMsg;
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
    public Set<ZSetOperations.TypedTuple<String>> getHotC10nPage(Double hotMaxMsgId, Long pageSize) {
        if (hotMaxMsgId == null) {
            hotMaxMsgId = Double.MAX_VALUE;
        }
        return RedisUtils.zReverseRangeByScoreWithScores(getKey(),
                hotMaxMsgId, pageSize);
    }

    /**
     * 获取指定热点会话最新消息id。
     *
     * @param roomIds
     * @return {@link Map }<{@link Long }, {@link Long }> roomId-lastMsgId
     */
    public List<HotC10nLastMsg> getHotC10nLastMsgIds(List<Long> roomIds) {
        if (roomIds == null || roomIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 批量获取 score
        List<Double> scores = RedisUtils.zMScore(getKey(), roomIds);
        if (scores.isEmpty()) {
            return Collections.emptyList();
        }

        List<HotC10nLastMsg> result = new ArrayList<>();
        for (int i = 0; i < roomIds.size(); i++) {
            Long roomId = roomIds.get(i);
            Double score = scores.get(i);
            result.add(new HotC10nLastMsg(roomId, score != null ? score.longValue() : null));
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
