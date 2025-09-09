package com.ershi.chat.websocket.service;

import com.ershi.user.domain.entity.UserEntity;
import com.ershi.user.domain.vo.UserLoginVO;
import io.netty.channel.Channel;

/**
 * @author Ershi-Gu.
 * @since 2025-09-08
 */
public interface ChatWebSocketService {

    /**
     * 根据携带token认证用户信息
     *
     * @param channel
     */
    void authorize(Channel channel);

    /**
     * 用户上线
     *
     * @param channel
     * @param loginUser
     */
    UserLoginVO online(Channel channel, UserEntity loginUser);

    /**
     * 用户下线
     *
     * @param channel
     */
    void offline(Channel channel);
}
