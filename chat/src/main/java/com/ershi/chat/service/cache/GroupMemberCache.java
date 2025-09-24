package com.ershi.chat.service.cache;

import com.ershi.chat.domain.GroupMemberEntity;
import com.ershi.chat.mapper.GroupMemberMapper;
import com.ershi.common.constants.RedisKey;
import com.ershi.common.utils.RedisUtils;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.ershi.chat.domain.table.GroupMemberEntityTableDef.GROUP_MEMBER_ENTITY;

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
        Set<String> roomMemberStr = RedisUtils.sGet(getRoomIdKey(roomId));
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
        RedisUtils.sSet(getRoomIdKey(roomId), uidList.toArray());
        return uidList;
    }

    /**
     * 删除缓存
     *
     * @param roomId
     */
    public void removeByRoomId(Long roomId) {
        RedisUtils.setRemove(RedisKey.getKey(getRoomIdKey(roomId)));
    }


    private String getRoomIdKey(Long roomId) {
        return RedisKey.getKey(RedisKey.ROOM_MEMBERS_BY_ROOM_ID_KEY, roomId);
    }

    /**
     * 根据groupId和uid获取用户在群组中的信息
     *
     * @param groupId
     * @param uid
     * @return {@link GroupMemberEntity }
     */
    public GroupMemberEntity getByGroupIdAndUid(Long groupId, Long uid) {
        // by cache
        GroupMemberEntity groupMemberEntity = RedisUtils.get(getGroupAndUidKey(groupId, uid), GroupMemberEntity.class);
        if (groupMemberEntity!=null) {
            return groupMemberEntity;
        }

        // by db
        groupMemberEntity = groupMemberMapper.selectOneByQuery(QueryWrapper.create()
                .where(GROUP_MEMBER_ENTITY.GROUP_ID.eq(groupId)
                        .and(GROUP_MEMBER_ENTITY.UID.eq(uid))));

        // set cache
        RedisUtils.set(getGroupAndUidKey(groupId, uid), groupMemberEntity);

        return groupMemberEntity;
    }

    public void removeByGroupIdAndUid(Long groupId, Long uid) {
        RedisUtils.del(RedisKey.getKey(getGroupAndUidKey(groupId, uid)));
    }

    private String getGroupAndUidKey(Long groupId, Long uid) {
        return RedisKey.getKey(RedisKey.ROOM_MEMBERS_BY_GROUP_ID_UID_KEY, groupId, uid);
    }
}
