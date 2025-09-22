package com.ershi.chat.service.cache;

import cn.hutool.core.lang.Pair;
import com.ershi.common.constants.RedisKey;
import com.ershi.common.domain.dto.CursorPageBaseReq;
import com.ershi.common.domain.vo.CursorPageBaseResp;
import com.ershi.common.utils.CursorUtils;
import com.ershi.common.utils.RedisUtils;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
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
     * 游标查询热点会话活跃时间 [roomId->active_time]
     *
     * @param pageBaseReq
     * @return
     */
    public CursorPageBaseResp<Pair<Long, Double>> getRoomCursorPage(CursorPageBaseReq pageBaseReq) {
        return CursorUtils.getCursorPageByRedis(pageBaseReq, RedisKey.getKey(RedisKey.HOT_ROOM_ACTIVE_TIME_KEY), Long::parseLong);
    }

    /**
     * 获取热点会话活跃时间在hotRecent和hotOld之间的房间，包含边界
     *
     * @param hotOld
     * @param hotRecent
     * @return {@link Set }<{@link ZSetOperations.TypedTuple }<{@link String }>>
     */
    public Set<ZSetOperations.TypedTuple<String>> getRoomRange(Double hotOld, Double hotRecent) {
        return RedisUtils.zRangeByScoreWithScores(RedisKey.getKey(RedisKey.HOT_ROOM_ACTIVE_TIME_KEY), hotOld, hotRecent);
    }

    /**
     * 更新指定热门群会话在记录表中的最新会话时间点-用于热门会话排序
     *
     * @param roomId
     * @param refreshTime
     */
    public void refreshActiveTime(Long roomId, LocalDateTime refreshTime) {
        long millis = refreshTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        RedisUtils.zAdd(RedisKey.getKey(RedisKey.HOT_ROOM_ACTIVE_TIME_KEY), roomId, (double) millis);
    }
}
