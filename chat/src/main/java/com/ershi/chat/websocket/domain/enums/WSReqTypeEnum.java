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
    AUTHORIZE(2, "用户身份认证"),
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
