package com.ershi.chat.event;

import com.ershi.chat.domain.UserApplyEntity;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 好友申请事件
 *
 * @author Ershi-Gu.
 * @since 2025-09-09
 */
@Getter
public class ApplyFriendEvent extends ApplicationEvent {

    private final UserApplyEntity userApplyEntity;

    public ApplyFriendEvent(Object source, UserApplyEntity userApplyEntity) {
        super(source);
        this.userApplyEntity = userApplyEntity;
    }
}
