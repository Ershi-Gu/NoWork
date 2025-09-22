package com.ershi.chat.websocket.domain.vo;

import lombok.Data;

/**
 * 服务端消息接收确认ack
 *
 * @author Ershi-Gu.
 * @since 2025-09-18
 */
@Data
public class CMReceiveAckResp {

    /**
     * 客户端消息确认唯一ackId
     */
    private Long clientMsgId;

    public static CMReceiveAckResp build(Long msgId) {
        CMReceiveAckResp ackResp = new CMReceiveAckResp();
        ackResp.setClientMsgId(msgId);
        return ackResp;
    }
}
