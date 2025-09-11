package com.ershi.chat.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * 房间类别枚举类
 *
 * @author Ershi
 * @date 2024/12/30
 */
@AllArgsConstructor
@Getter
public enum RoomTypeEnum {

    ALL(0, "全员群"),
    GROUP(1, "群聊"),
    FRIEND(2, "单聊"),
    ;

    private final Integer type;
    private final String desc;

    private static final Map<Integer, RoomTypeEnum> CACHE;

    static {
        CACHE = Arrays.stream(RoomTypeEnum.values()).collect(Collectors.toMap(RoomTypeEnum::getType, Function.identity()));
    }

    public static RoomTypeEnum of(Integer type) {
        return CACHE.get(type);
    }
}
