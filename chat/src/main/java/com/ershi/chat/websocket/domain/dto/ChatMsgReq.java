package com.ershi.chat.websocket.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 聊天消息发送请求
 *
 * @author Ershi
 * @since 2025-09-17
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMsgReq {

    /**
     * 发送目标房间id
     */
    private Long roomId;

    /**
     * 发送者用户id
     */
    private Long senderId;

    /**
     * 消息类型
     */
    private Integer msgType;

    /**
     * 消息内容
     */
    private String messageBody;

}
