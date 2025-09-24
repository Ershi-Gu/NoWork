package com.ershi.chat.websocket.service;

import com.ershi.chat.domain.vo.ChatMessageResp;
import com.ershi.chat.websocket.domain.vo.WSBaseResp;
import com.ershi.user.domain.entity.UserEntity;
import com.ershi.user.domain.vo.UserLoginVO;
import io.netty.channel.Channel;

import java.util.List;

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

    /**
     * 接收客户端发送的聊天消息
     *
     * @param channel
     * @param data
     */
    void receiveChatMsg(Channel channel, String data);

    /**
     * 获取channel在线用户uid
     *
     * @return {@link List }<{@link Long }>
     */
    List<Long> getOnlineUids();

    /**
     * 指定收信人发送消息
     *
     * @param memberUidList
     * @param wsResp
     */
    void sendMsgToUser(List<Long> memberUidList, WSBaseResp<?> wsResp);

    /**
     * 发送全体消息
     *
     * @param wsResp
     */
    void sendMsgToAllUser(WSBaseResp<?> wsResp);

    /**
     * 客户端接收消息确认ack
     *
     * @param data
     */
    void confirmMsgAck(String data);
}
