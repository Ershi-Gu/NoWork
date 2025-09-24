package com.ershi.chat.service.cache;

import com.ershi.chat.domain.RoomGroupEntity;
import com.ershi.chat.mapper.RoomGroupMapper;
import com.ershi.common.cache.cache.AbstractRedisStringCache;
import com.ershi.common.constants.RedisKey;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.ershi.chat.domain.table.RoomGroupEntityTableDef.ROOM_GROUP_ENTITY;

/**
 *
 * @author Ershi-Gu.
 * @since 2025-09-18
 */
@Service
public class RoomGroupByRoomIdCache extends AbstractRedisStringCache<Long, RoomGroupEntity> {

    @Resource
    private RoomGroupMapper roomGroupMapper;

    public static final long ROOM_GROUP_EXPIRE_SECONDS = 5 * 60L;

    @Override
    protected String getKey(Long roomId) {
        return RedisKey.getKey(RedisKey.ROOM_GROUP_BY_ROOM_ID_KEY, roomId);
    }

    @Override
    protected Long getExpireSeconds() {
        return ROOM_GROUP_EXPIRE_SECONDS;
    }

    @Override
    protected Map<Long, RoomGroupEntity> load(List<Long> roomId) {
        List<RoomGroupEntity> roomGroups = roomGroupMapper.selectListByQuery(QueryWrapper.create()
                .where(ROOM_GROUP_ENTITY.ROOM_ID.in(roomId)));
        return roomGroups.stream().collect(Collectors.toMap(RoomGroupEntity::getRoomId, Function.identity()));
    }
}
