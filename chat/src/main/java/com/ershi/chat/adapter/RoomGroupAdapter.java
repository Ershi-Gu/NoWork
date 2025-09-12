package com.ershi.chat.adapter;

import com.ershi.chat.constants.GroupAvatarConstant;
import com.ershi.chat.domain.RoomEntity;
import com.ershi.chat.domain.RoomGroupEntity;
import com.ershi.user.domain.entity.UserEntity;

/**
 * 群组构造器
 *
 * @author Ershi-Gu.
 * @since 2025-09-12
 */
public class RoomGroupAdapter {

    /**
     * 创建群组信息
     *
     * @param roomEntity
     * @param leader
     * @return {@link RoomGroupEntity }
     */
    public static RoomGroupEntity buildRoomGroup(RoomEntity roomEntity, UserEntity leader) {
        return RoomGroupEntity.builder()
                .roomId(roomEntity.getId())
                // 加一个3位随机整数
                .name(leader.getName() + "的群聊_" + (int) (Math.random() * 1000))
                // 默认头像
                .avatarUrl(GroupAvatarConstant.DEFAULT_AVATAR_URL)
                .build();
    }
}
