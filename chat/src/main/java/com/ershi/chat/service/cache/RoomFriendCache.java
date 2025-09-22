package com.ershi.chat.service.cache;

import com.ershi.chat.domain.RoomFriendEntity;
import com.ershi.chat.mapper.RoomFriendMapper;
import com.ershi.common.cache.cache.AbstractRedisStringCache;
import com.ershi.common.constants.RedisKey;
import com.ershi.common.utils.RedisUtils;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.ershi.chat.domain.table.RoomFriendEntityTableDef.ROOM_FRIEND_ENTITY;

/**
 * 单聊房间缓存
 *
 * @author Ershi-Gu.
 * @since 2025-09-18
 */
@Component
public class RoomFriendCache {

    @Resource
    private RoomFriendMapper roomFriendMapper;

    public static final long ROOM_FRIEND_EXPIRE_SECONDS = 5 * 60L;

    /**
     * 通过roomId获取RoomFriendEntity，旁路缓存模式
     *
     * @param roomId
     * @return {@link RoomFriendEntity }
     */
    public RoomFriendEntity getRoomFriendByRoomId(Long roomId) {
        RoomFriendEntity roomFriendEntity = RedisUtils.get(getKey(roomId), RoomFriendEntity.class);
        if (roomFriendEntity != null) {
            return roomFriendEntity;
        }

        roomFriendEntity = roomFriendMapper.selectOneByQuery(QueryWrapper.create()
                .where(ROOM_FRIEND_ENTITY.ROOM_ID.eq(roomId)));
        RedisUtils.set(getKey(roomId), roomFriendEntity, ROOM_FRIEND_EXPIRE_SECONDS);
        return roomFriendEntity;
    }

    protected String getKey(Long roomId) {
        return RedisKey.getKey(RedisKey.ROOM_FRIEND_KEY, roomId);
    }
}
