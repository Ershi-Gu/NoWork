package com.ershi.chat.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * 房间热点标记枚举类
 *
 * @author Ershi
 * @date 2024/12/30
 */
@AllArgsConstructor
@Getter
public enum RoomHotFlagEnum {

    NOT_HOT(0, "非热点"),
    HOT(1, "热点"),
    ;

    private final Integer type;
    private final String desc;

    private static final Map<Integer, RoomHotFlagEnum> CACHE;

    static {
        CACHE = Arrays.stream(RoomHotFlagEnum.values()).collect(Collectors.toMap(RoomHotFlagEnum::getType, Function.identity()));
    }

    public static RoomHotFlagEnum of(Integer type) {
        return CACHE.get(type);
    }
}
