package com.ershi.chat.websocket.domain.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 消息已读请求
 *
 * @author Ershi-Gu.
 * @since 2025-09-29
 */
@Data
public class MsgReadReq {

    private Long uid;

    private Long roomId;

    private Long msgId;
}
