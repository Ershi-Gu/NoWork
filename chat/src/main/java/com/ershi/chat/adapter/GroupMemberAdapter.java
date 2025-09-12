package com.ershi.chat.adapter;

import com.ershi.chat.domain.GroupMemberEntity;
import com.ershi.chat.domain.RoomGroupEntity;
import com.ershi.chat.domain.enums.GroupMemberRoleEnum;
import com.ershi.user.domain.entity.UserEntity;

import java.util.Collections;
import java.util.List;

/**
 * 群成员信息构造器
 *
 * @author Ershi-Gu.
 * @since 2025-09-12
 */
public class GroupMemberAdapter {

    /**
     * 构建群主信息
     *
     * @param userEntity
     * @param roomGroupEntity
     * @return {@link GroupMemberEntity }
     */
    public static GroupMemberEntity buildGroupLeader(UserEntity userEntity, RoomGroupEntity roomGroupEntity) {
        return GroupMemberEntity.builder()
                .groupId(roomGroupEntity.getId())
                .uid(userEntity.getId())
                .role(GroupMemberRoleEnum.LEADER.getType())
                .build();
    }

    /**
     * 构建群成员信息（可批量）
     *
     * @param userEntities    用户实体列表
     * @param roomGroupEntity 群实体
     * @param roleEnum        成员角色
     * @return {@link List<GroupMemberEntity>}
     */
    public static List<GroupMemberEntity> buildGroupMembers(List<UserEntity> userEntities,
                                                            RoomGroupEntity roomGroupEntity,
                                                            GroupMemberRoleEnum roleEnum) {
        if (userEntities == null || userEntities.isEmpty()) {
            return Collections.emptyList();
        }
        return userEntities.stream()
                .map(user -> GroupMemberEntity.builder()
                        .groupId(roomGroupEntity.getId())
                        .uid(user.getId())
                        .role(roleEnum.getType())
                        .build())
                .toList();
    }
}
