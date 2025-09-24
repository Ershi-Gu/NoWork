package com.ershi.chat.event;

import com.ershi.chat.domain.GroupMemberEntity;
import com.ershi.chat.domain.RoomGroupEntity;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.List;

/**
 * 新建群组信息
 *
 * @author Ershi-Gu.
 * @since 2025-09-12
 */
@Getter
public class CreateRoomGroupEvent extends ApplicationEvent {

    private final RoomGroupEntity roomGroupEntity;

    private final List<GroupMemberEntity> groupMemberList;

    private final Long inviteUid;

    public CreateRoomGroupEvent(Object source, RoomGroupEntity roomGroupEntity, List<GroupMemberEntity> groupMemberList, Long inviteUid) {
        super(source);
        this.roomGroupEntity = roomGroupEntity;
        this.groupMemberList = groupMemberList;
        this.inviteUid = inviteUid;
    }
}
