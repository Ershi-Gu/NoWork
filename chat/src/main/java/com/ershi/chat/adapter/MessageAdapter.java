package com.ershi.chat.adapter;

import com.ershi.chat.domain.enums.YesOrNoEnum;
import com.ershi.chat.domain.message.MessageEntity;
import com.ershi.chat.domain.message.ReplyTextMsgDTO;
import com.ershi.chat.domain.message.TextMsgDTO;
import com.ershi.chat.service.handler.message.MsgHandlerFactory;
import com.ershi.chat.websocket.domain.dto.ChatMsgReq;
import com.ershi.user.domain.entity.UserEntity;

import java.util.Objects;

/**
 * 多类型消息转换适配器
 *
 * @author Ershi-Gu.
 * @since 2025-09-17
 */
public class MessageAdapter {

    /**
     * 用于判断消息回复是否可以跳转，最大支持100条间隔
     */
    public static final int CAN_CALLBACK_GAP_COUNT = 100;

    /**
     * 将消息请求转换为基本消息体，不包含具体messageBody（由子类实现具体内容保存）
     *
     * @param chatMsgReq
     * @return {@link MessageEntity }
     */
    public static MessageEntity buildBaseMessageEntity(ChatMsgReq chatMsgReq) {
        return MessageEntity.builder()
                .roomId(chatMsgReq.getRoomId())
                .senderId(chatMsgReq.getSenderId())
                .type(chatMsgReq.getMsgType())
                .build();
    }

    /**
     * 构建回复消息的展示体
     *
     * @param replyMessage
     * @return {@link Object }
     */
    public static ReplyTextMsgDTO buildReplyMessage(TextMsgDTO msg, MessageEntity replyMessage,
                                                    UserEntity replyUserInfo) {
        return ReplyTextMsgDTO.builder()
                .id(replyMessage.getId())
                .uid(replyMessage.getSenderId())
                .userName(replyUserInfo.getName())
                .type(replyMessage.getType())
                .body(MsgHandlerFactory.getMsgHandlerNoNull(replyMessage.getType()).showReplyMsg(replyMessage))
                .canCallback(YesOrNoEnum.toStatus(
                        Objects.nonNull(msg.getGapCountToReply())
                                && msg.getGapCountToReply() <= MessageAdapter.CAN_CALLBACK_GAP_COUNT)
                )
                .gapCount(msg.getGapCountToReply()).build();
    }
}
