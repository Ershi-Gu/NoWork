package com.ershi.chat.websocket.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * websocket 前端请求类型枚举
 *
 * @author Ershi
 * @date 2024/11/24
 */
@AllArgsConstructor
@Getter
public enum WSReqTypeEnum {

    HEARTBEAT("heartbeat", "心跳包"),
    MESSAGE("send_message", "客户端发送消息"),
    MSG_RECEIVE_ACK("client_ack", "客户端接收聊天消息确认"),
    MSG_READ("mark_read", "标记消息已读"),
    ;

    /**
     * 请求类型
     */
    private final String type;
    /**
     * 请求描述
     */
    private final String desc;


    /**
     * 枚举类型缓存
     */
    private static final Map<String, WSReqTypeEnum> CACHE;

    static {
        CACHE = Arrays.stream(WSReqTypeEnum.values()).collect(Collectors.toMap(WSReqTypeEnum::getType, Function.identity()));
    }

    /**
     * 获取枚举类
     * @param type
     * @return {@link WSReqTypeEnum}
     */
    public static WSReqTypeEnum of(String type) {
        return CACHE.get(type);
    }
}
