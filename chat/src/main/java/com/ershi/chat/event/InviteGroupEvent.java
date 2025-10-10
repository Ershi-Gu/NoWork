package com.ershi.chat.event;

import com.ershi.chat.domain.GroupInviteEntity;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.List;

/**
 * 邀请入群事件
 *
 * @author Ershi_Gu
 * @since 2025/10/10
 */
@Getter
public class InviteGroupEvent extends ApplicationEvent {

    private final List<GroupInviteEntity> inviteList;

    public InviteGroupEvent(Object source, List<GroupInviteEntity> inviteList) {
        super(source);
        this.inviteList = inviteList;
    }
}