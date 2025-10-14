package com.ershi.chat.websocket.domain.vo;

import lombok.Data;

/**
 * ws 服务端基本推送体
 *
 * @author Ershi
 * @date 2024/11/24
 */
@Data
public class WSBaseResp<T> {

    /**
     * ws 服务端推送给前端的消息类型
     *
     * @see com.ershi.chat.websocket.domain.enums.WSRespTypeEnum
     */
    private String type;

    /**
     * 根据不同的消息 type 规定不同的返回对象类型
     */
    private T data;


    /**
     * ws基本推送体构建方法
     * @param type
     * @param data
     * @return {@link WSBaseResp}<{@link T}>
     */
    public static <T> WSBaseResp<T> build(String type, T data) {
        WSBaseResp<T> wsBaseResp = new WSBaseResp<>();
        wsBaseResp.setType(type);
        wsBaseResp.setData(data);
        return wsBaseResp;
    }
}
