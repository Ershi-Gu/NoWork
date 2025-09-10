package com.ershi.chat.event;

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


    public FriendAddedEvent(Object source) {
        super(source);
    }
}
