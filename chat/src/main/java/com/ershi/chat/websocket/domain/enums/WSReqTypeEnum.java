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

    HEARTBEAT(1, "心跳包"),
    MESSAGE(2, "接收客户端发送的消息"),
    MSG_RECEIVE_ACK(3, "客户端接收聊天消息确认"),
    MSG_READ(4, "消息已读标记"),
    ;

    /**
     * 请求类型
     */
    private final Integer type;
    /**
     * 请求描述
     */
    private final String desc;


    /**
     * 枚举类型缓存
     */
    private static final Map<Integer, WSReqTypeEnum> CACHE;

    static {
        CACHE = Arrays.stream(WSReqTypeEnum.values()).collect(Collectors.toMap(WSReqTypeEnum::getType, Function.identity()));
    }

    /**
     * 获取枚举类
     * @param type
     * @return {@link WSReqTypeEnum}
     */
    public static WSReqTypeEnum of(Integer type) {
        return CACHE.get(type);
    }
}
