package com.ershi.chat.service.adapter;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.ershi.chat.domain.message.MessageEntity;
import com.ershi.chat.domain.vo.ChatMessageResp;
import com.ershi.chat.service.handler.message.AbstractMsgHandler;
import com.ershi.chat.service.handler.message.MsgHandlerFactory;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 服务端返回消息转换器
 *
 * @author Ershi-Gu.
 * @since 2025-09-23
 */
public class MsgAdapter {

    /**
     * 构建单个服务端聊天消息发送体
     *
     * @param messageEntity
     * @return {@link ChatMessageResp }
     */
    public static ChatMessageResp buildChatMsgResp(MessageEntity messageEntity) {
        return CollUtil.getFirst(batchBuildChatMsgResp(Collections.singletonList(messageEntity)));
    }

    /**
     * 批量构建服务端聊天消息发送体
     *
     * @param messageEntities
     * @return {@link List }<{@link ChatMessageResp }>
     */
    public static List<ChatMessageResp> batchBuildChatMsgResp(List<MessageEntity> messageEntities) {
        return messageEntities.stream().map(message -> {
                    // 组装服务端聊天消息发送体
                    return ChatMessageResp.builder()
                            .senderUid(message.getSenderId())
                            .messageInfo(buildMsgInfo(message))
                            .build();
                })
                // 按照时序性排序（id即保证时序）-> 升序，前发送的在前
                .sorted(Comparator.comparing(message -> message.getMessageInfo().getId()))
                .collect(Collectors.toList());
    }

    /**
     * 构建消息信息对象
     * <p>
     * 该方法用于将消息实体转换为响应消息信息，包括复制基本属性、设置发送时间，
     * 以及根据消息类型获取对应的消息处理器来格式化消息内容。
     *
     * @param messageEntity
     * @return {@link ChatMessageResp.MessageInfo }
     */
    private static ChatMessageResp.MessageInfo buildMsgInfo(MessageEntity messageEntity) {
        // copy基本信息
        ChatMessageResp.MessageInfo messageInfo = new ChatMessageResp.MessageInfo();
        BeanUtil.copyProperties(messageEntity, messageInfo);
        messageInfo.setSendTime(messageEntity.getCreateTime());

        // 获取多类型消息转换处理器
        AbstractMsgHandler<?> msgHandler = MsgHandlerFactory.getMsgHandlerNoNull(messageEntity.getType());
        if (Objects.nonNull(msgHandler)) {
            // 获取多类型消息展示格式
            messageInfo.setBody(msgHandler.showMsg(messageEntity));
        }

        return messageInfo;
    }
}
