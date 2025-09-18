package com.ershi.chat.service;


import com.ershi.chat.domain.message.MessageEntity;
import com.ershi.chat.websocket.domain.dto.ChatMsgReq;
import com.mybatisflex.core.service.IService;

/**
 * 消息表 服务层。
 *
 * @author mybatis-flex-helper automatic generation
 * @since 1.0
 */
public interface IMessageService extends IService<MessageEntity> {

    /**
     * 持久化消息并通过ws发送
     *
     * @param chatMsgReq
     */
    void sendMultiTypeMessage(ChatMsgReq chatMsgReq);
}