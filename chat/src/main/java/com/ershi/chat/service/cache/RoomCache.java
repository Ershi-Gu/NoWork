package com.ershi.chat.service.cache;

import com.ershi.chat.domain.RoomEntity;
import com.ershi.chat.mapper.RoomMapper;
import com.ershi.common.cache.cache.AbstractRedisStringCache;
import com.ershi.common.constants.RedisKey;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 房间缓存
 *
 * @author Ershi-Gu.
 * @since 2025-09-18
 */
@Service
public class RoomCache extends AbstractRedisStringCache<Long, RoomEntity> {

    @Resource
    private RoomMapper roomMapper;

    public static final long ROOM_INFO_EXPIRE_SECONDS = 5 * 60L;

    @Override
    protected String getKey(Long roomId) {
        return RedisKey.getKey(RedisKey.ROOM_KEY, roomId);
    }

    @Override
    protected Long getExpireSeconds() {
        return ROOM_INFO_EXPIRE_SECONDS;
    }

    @Override
    protected Map<Long, RoomEntity> load(List<Long> roomIdList) {
        List<RoomEntity> rooms = roomMapper.selectListByIds(roomIdList);
        return rooms.stream().collect(Collectors.toMap(RoomEntity::getId, Function.identity()));
    }
}
