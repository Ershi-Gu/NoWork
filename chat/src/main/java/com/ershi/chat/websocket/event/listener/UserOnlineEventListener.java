package com.ershi.chat.websocket.event.listener;

import com.ershi.user.domain.vo.UserLoginVO;
import com.ershi.chat.websocket.event.UserOnlineEvent;
import com.ershi.user.service.cache.UserInfoCache;
import jakarta.annotation.Resource;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 用户上线事件监听
 *
 * @author Ershi-Gu.
 * @since 2025-09-09
 */
@Component
public class UserOnlineEventListener {

    @Resource
    private UserInfoCache userInfoCache;

    /**
     * 更新在线用户表，同时发送用户上线通知
     *
     * @param userOnlineEvent
     */
    @Async
    @EventListener(classes = UserOnlineEvent.class)
    public void updateOnlineAndPush(UserOnlineEvent userOnlineEvent) {
        UserLoginVO userLoginVO = userOnlineEvent.getUserLoginVO();

        // 更新在线表
        userInfoCache.online(userLoginVO);

        // todo 推送用户上线通知
    }
}
