package com.ershi.chat.websocket.event.listener;

import com.ershi.chat.websocket.event.UserOfflineEvent;
import com.ershi.user.domain.entity.UserEntity;
import com.ershi.user.mapper.UserMapper;
import com.ershi.user.service.cache.UserInfoCache;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 用户下线事件监听
 *
 * @author Ershi-Gu.
 * @since 2025-09-09
 */
@Slf4j
@Component
public class UserOfflineEventListener {

    @Resource
    private UserInfoCache userInfoCache;

    @Resource
    private UserMapper userMapper;

    /**
     * 更新在线用户表，同时发送用户上线通知
     *
     * @param userOfflineEvent
     */
    @Async
    @EventListener(classes = UserOfflineEvent.class)
    public void updateOfflineAndPush(UserOfflineEvent userOfflineEvent) {
        UserEntity userEntity = userOfflineEvent.getUserEntity();

        // 更新Redis离线用户表
        userInfoCache.offline(userEntity.getId(), userEntity.getLastLoginTime());

        // todo 推送用户下线消息
    }

    /**
     * 更新db用户表信息
     *
     * @param userOfflineEvent
     */
    @Async
    @EventListener(classes = UserOfflineEvent.class)
    public void updateUserToDb(UserOfflineEvent userOfflineEvent) {
        UserEntity user = userOfflineEvent.getUserEntity();
        boolean updated = userMapper.update(user) > 0;
        if (!updated) {
            log.error("用户id：{} 下线更新db数据失败，请检查", user.getId());
        }
    }
}
