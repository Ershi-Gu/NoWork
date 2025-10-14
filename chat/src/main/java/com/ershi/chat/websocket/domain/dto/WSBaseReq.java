package com.ershi.chat.websocket.domain.dto;

import lombok.Data;

/**
 * websocket 前端基本请求体
 * @author Ershi
 * @date 2024/11/24
 */
@Data
public class WSBaseReq {

    /**
     * 请求类型：
     * @see com.ershi.chat.websocket.domain.enums.WSReqTypeEnum
     */
    private String type;

    /**
     * 请求包数据
     */
    private String data;
}
