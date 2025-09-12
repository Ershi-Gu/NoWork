package com.ershi.chat.event;

import com.ershi.chat.domain.GroupMemberEntity;
import com.ershi.chat.domain.RoomGroupEntity;
import org.springframework.context.ApplicationEvent;

import java.util.List;

/**
 * 新建群组信息
 *
 * @author Ershi-Gu.
 * @since 2025-09-12
 */
public class CreateRoomGroupEvent extends ApplicationEvent {

    private final RoomGroupEntity roomGroupEntity;

    private final List<GroupMemberEntity> groupMemberList;

    public CreateRoomGroupEvent(Object source, RoomGroupEntity roomGroupEntity, List<GroupMemberEntity> groupMemberList) {
        super(source);
        this.roomGroupEntity = roomGroupEntity;
        this.groupMemberList = groupMemberList;
    }
}
