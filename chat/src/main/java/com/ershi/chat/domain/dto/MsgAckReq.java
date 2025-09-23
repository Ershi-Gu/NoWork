package com.ershi.chat.domain.dto;

import lombok.Data;

/**
 * 客户端接收消息确认ack
 *
 * @author Ershi-Gu.
 * @since 2025-09-23
 */
@Data
public class MsgAckReq {

    /**
     * 接收者uid
     */
    private Long uid;

    /**
     * 消息uid
     */
    private Long msgId;
}
