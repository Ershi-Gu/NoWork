package com.ershi.chat.websocket.domain.vo;

import lombok.Data;

/**
 * WS 异常消息返回
 *
 * @author Ershi-Gu.
 * @since 2025-09-17
 */
@Data
public class WSErrorResp {

    /**
     * 错误信息
     */
    private String errorMsg;

    public static WSErrorResp build(String errorMsg) {
        WSErrorResp wsErrorResp = new WSErrorResp();
        wsErrorResp.setErrorMsg(errorMsg);
        return wsErrorResp;
    }
}
