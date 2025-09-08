package com.ershi.chat.websocket.event;

import com.ershi.user.domain.entity.UserEntity;
import com.ershi.user.domain.vo.UserLoginVO;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 用户上线事件
 *
 * @author Ershi-Gu.
 * @since 2025-09-09
 */
@Getter
public class UserOnlineEvent extends ApplicationEvent {

    private final UserEntity userEntity;

    private final UserLoginVO userLoginVO;

    public UserOnlineEvent(Object source, UserEntity user, UserLoginVO userLoginVO) {
        super(source);
        this.userEntity = user;
        this.userLoginVO = userLoginVO;
    }
}
