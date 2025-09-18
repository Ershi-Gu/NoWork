package com.ershi.chat.service.cache;

import com.ershi.chat.domain.RoomFriendEntity;
import com.ershi.chat.domain.RoomGroupEntity;
import com.ershi.chat.mapper.RoomGroupMapper;
import com.ershi.common.cache.cache.AbstractRedisStringCache;
import com.ershi.common.constants.RedisKey;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *
 * @author Ershi-Gu.
 * @since 2025-09-18
 */
@Service
public class RoomGroupCache extends AbstractRedisStringCache<Long, RoomGroupEntity> {

    @Resource
    private RoomGroupMapper roomGroupMapper;

    public static final long ROOM_GROUP_EXPIRE_SECONDS = 5 * 60L;

    @Override
    protected String getKey(Long roomGroupId) {
        return RedisKey.getKey(RedisKey.ROOM_GROUP_KEY, roomGroupId);
    }

    @Override
    protected Long getExpireSeconds() {
        return ROOM_GROUP_EXPIRE_SECONDS;
    }

    @Override
    protected Map<Long, RoomGroupEntity> load(List<Long> roomGroupId) {
        List<RoomGroupEntity> rooms = roomGroupMapper.selectListByIds(roomGroupId);
        return rooms.stream().collect(Collectors.toMap(RoomGroupEntity::getId, Function.identity()));
    }
}
