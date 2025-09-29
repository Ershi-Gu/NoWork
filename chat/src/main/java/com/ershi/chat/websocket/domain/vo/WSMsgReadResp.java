package com.ershi.chat.websocket.domain.vo;

import lombok.Data;

/**
 * 消息已读操作回复
 *
 * @author Ershi-Gu.
 * @since 2025-09-29
 */
@Data
public class WSMsgReadResp {

    private Long uid;

    private Long roomId;

    public static WSMsgReadResp build(Long uid, Long roomId) {
        WSMsgReadResp wsMsgReadResp = new WSMsgReadResp();
        wsMsgReadResp.setUid(uid);
        wsMsgReadResp.setRoomId(roomId);
        return wsMsgReadResp;
    }
}
