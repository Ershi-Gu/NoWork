package com.ershi.chat.websocket.domain.vo;

import lombok.Data;

/**
 * 消息ack操作回复
 *
 * @author Ershi-Gu.
 * @since 2025-10-09
 */
@Data
public class WSMsgAckResp {

    /**
     * 接收者uid
     */
    private Long uid;

    /**
     * 消息uid
     */
    private Long msgId;

    public static WSMsgAckResp build(Long uid, Long msgId) {
        WSMsgAckResp wsMsgAckResp = new WSMsgAckResp();
        wsMsgAckResp.setUid(uid);
        wsMsgAckResp.setMsgId(msgId);
        return wsMsgAckResp;
    }
}