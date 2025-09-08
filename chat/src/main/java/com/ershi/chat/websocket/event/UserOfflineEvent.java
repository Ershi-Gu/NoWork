package com.ershi.chat.websocket.event;

import com.ershi.user.domain.entity.UserEntity;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 用户下线事件
 *
 * @author Ershi-Gu.
 * @since 2025-09-09
 */
@Getter
public class UserOfflineEvent extends ApplicationEvent {

    private final UserEntity user;

    public UserOfflineEvent(Object source, UserEntity user) {
        super(source);
        this.user = user;
    }
}
