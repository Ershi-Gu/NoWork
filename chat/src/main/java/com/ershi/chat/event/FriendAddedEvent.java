package com.ershi.chat.event;

import com.ershi.chat.domain.RoomEntity;
import com.ershi.chat.domain.RoomFriendEntity;
import com.ershi.chat.domain.UserApplyEntity;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 好友关系建立后事件
 *
 * @author Ershi-Gu.
 * @since 2025-09-09
 */
@Getter
public class FriendAddedEvent extends ApplicationEvent {

    private final Long uid;

    private final RoomFriendEntity roomFriendEntity;

    public FriendAddedEvent(Object source, Long uid, RoomFriendEntity roomFriendEntity) {
        super(source);
        this.uid = uid;
        this.roomFriendEntity = roomFriendEntity;
    }
}
