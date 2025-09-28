package com.ershi.chat.service.cache;

import com.ershi.chat.domain.RoomFriendEntity;
import com.ershi.chat.mapper.RoomFriendMapper;
import com.ershi.common.cache.cache.AbstractRedisStringCache;
import com.ershi.common.constants.RedisKey;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.ershi.chat.domain.table.RoomFriendEntityTableDef.ROOM_FRIEND_ENTITY;
import static com.ershi.chat.domain.table.RoomGroupEntityTableDef.ROOM_GROUP_ENTITY;

/**
 * 单聊房间缓存
 *
 * @author Ershi-Gu.
 * @since 2025-09-18
 */
@Component
public class RoomFriendCache extends AbstractRedisStringCache<Long, RoomFriendEntity> {

    public static final long ROOM_FRIEND_EXPIRE_SECONDS = 5 * 60L;

    @Resource
    private RoomFriendMapper roomFriendMapper;

    @Override
    protected String getKey(Long roomId) {
        return RedisKey.getKey(RedisKey.ROOM_FRIEND_KEY, roomId);
    }

    @Override
    protected Long getExpireSeconds() {
        return ROOM_FRIEND_EXPIRE_SECONDS;
    }

    @Override
    protected Map<Long, RoomFriendEntity> load(List<Long> roomId) {
        List<RoomFriendEntity> roomFriends = roomFriendMapper.selectListByQuery(QueryWrapper.create()
                .where(ROOM_FRIEND_ENTITY.ROOM_ID.in(roomId)));
        return roomFriends.stream().collect(Collectors.toMap(RoomFriendEntity::getRoomId, Function.identity()));
    }
}
