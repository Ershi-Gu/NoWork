package com.ershi.chat.websocket.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * websocket 后端推送类型枚举
 *
 * @author Ershi
 * @date 2024/11/24
 */
@AllArgsConstructor
@Getter
public enum WSRespTypeEnum {
    ERROR(-1, "错误消息"),
    AUTHORIZE_SUCCESS(1, "用户身份认证成功"),
    RECEIVE_ACK(2, "接收消息确认"),
    ;

    /**
     * 推送消息类型
     */
    private final Integer type;

    /**
     * 推送消息描述
     */
    private final String desc;

    /**
     * 枚举类缓存
     */
    private static final Map<Integer, WSRespTypeEnum> CACHE;

    static {
        CACHE = Arrays.stream(WSRespTypeEnum.values()).collect(Collectors.toMap(WSRespTypeEnum::getType, Function.identity()));
    }

    public static WSRespTypeEnum of(Integer type) {
        return CACHE.get(type);
    }
}
