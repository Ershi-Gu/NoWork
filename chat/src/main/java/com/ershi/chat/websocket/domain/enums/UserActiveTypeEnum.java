package com.ershi.chat.websocket.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 用户在线离线状态
 *
 * @author Ershi
 * @since 2025-09-09
 */
@AllArgsConstructor
@Getter
public enum UserActiveTypeEnum {

    OFFLINE(0, "离线"),
    ONLINE(1, "在线"),
    ;

    private final Integer type;

    private final String desc;


    /**
     * 枚举类型缓存
     */
    private static final Map<Integer, UserActiveTypeEnum> CACHE;

    static {
        CACHE = Arrays.stream(UserActiveTypeEnum.values()).collect(Collectors.toMap(UserActiveTypeEnum::getType, Function.identity()));
    }

    /**
     * 获取枚举类
     * @param type
     * @return {@link UserActiveTypeEnum}
     */
    public static UserActiveTypeEnum of(Integer type) {
        return CACHE.get(type);
    }
}
