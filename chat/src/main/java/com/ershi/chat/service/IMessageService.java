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

    /**
     * 异步持久化消息数据，该方法由本地事务表保证可靠性，并在内部进行异步调用
     *
     * @param messageEntity
     * @return {@link Long }
     */
    Long saveMessage(MessageEntity messageEntity);
}