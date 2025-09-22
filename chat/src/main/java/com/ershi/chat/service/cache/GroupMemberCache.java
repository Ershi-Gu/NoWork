package com.ershi.chat.service.cache;

import com.ershi.chat.domain.GroupMemberEntity;
import com.ershi.chat.mapper.GroupMemberMapper;
import com.ershi.common.constants.RedisKey;
import com.ershi.common.utils.RedisUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 群成员信息缓存
 *
 * @author Ershi-Gu.
 * @since 2025-09-22
 */
@Service
public class GroupMemberCache {

    public static final long ROOM_MEMBER_EXPIRE_SECONDS = 5 * 60L;

    @Resource
    private GroupMemberMapper groupMemberMapper;

    /**
     * 根据roomId获取房间成员uid
     *
     * @param roomId
     * @return {@link List }<{@link Long }>
     */
    public List<Long> getRoomMemberUidList(Long roomId) {
        // 从 Redis 获取房间成员 uid 字符串集合
        Set<String> roomMemberStr = RedisUtils.sGet(getKey(roomId));
        if (roomMemberStr != null && !roomMemberStr.isEmpty()) {
            return roomMemberStr.stream()
                    .map(Long::valueOf)
                    .collect(Collectors.toList());
        }

        // 缓存不存在，走db
        List<GroupMemberEntity> memberList = groupMemberMapper.getMemberListByRoomId(roomId);
        if (memberList == null || memberList.isEmpty()) {
            return Collections.emptyList();
        }

        // 提取 uid 列表
        List<Long> uidList = memberList.stream()
                .map(GroupMemberEntity::getUid)
                .collect(Collectors.toList());

        // 将 uid 列表写入 Redis 缓存
        RedisUtils.sSet(getKey(roomId), uidList.toArray());
        return uidList;
    }

    /**
     * 删除缓存
     *
     * @param roomId
     */
    public void remove(Long roomId) {
        RedisUtils.setRemove(RedisKey.getKey(getKey(roomId)));
    }


    private String getKey(Long roomId) {
        return RedisKey.getKey(RedisKey.ROOM_MEMBERS_KEY, roomId);
    }
}
