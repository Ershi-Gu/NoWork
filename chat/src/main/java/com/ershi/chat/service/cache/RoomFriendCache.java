package com.ershi.chat.service.cache;

import com.ershi.chat.domain.RoomEntity;
import com.ershi.chat.domain.RoomFriendEntity;
import com.ershi.chat.mapper.RoomFriendMapper;
import com.ershi.common.cache.cache.AbstractRedisStringCache;
import com.ershi.common.constants.RedisKey;
import jakarta.annotation.Resource;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 单聊房间缓存
 *
 * @author Ershi-Gu.
 * @since 2025-09-18
 */
public class RoomFriendCache extends AbstractRedisStringCache<Long, RoomFriendEntity> {

    @Resource
    private RoomFriendMapper roomFriendMapper;

    public static final long ROOM_FRIEND_EXPIRE_SECONDS = 5 * 60L;

    @Override
    protected String getKey(Long roomFriendId) {
        return RedisKey.getKey(RedisKey.ROOM_FRIEND_KEY, roomFriendId);
    }

    @Override
    protected Long getExpireSeconds() {
        return ROOM_FRIEND_EXPIRE_SECONDS;
    }

    @Override
    protected Map<Long, RoomFriendEntity> load(List<Long> roomFriendId) {
        List<RoomFriendEntity> rooms = roomFriendMapper.selectListByIds(roomFriendId);
        return rooms.stream().collect(Collectors.toMap(RoomFriendEntity::getId, Function.identity()));
    }
}
