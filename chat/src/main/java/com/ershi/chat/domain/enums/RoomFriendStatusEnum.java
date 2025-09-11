package com.ershi.chat.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 单聊房间状态枚举
 *
 * @author Ershi
 * @date 2024/12/30
 */
@AllArgsConstructor
@Getter
public enum RoomFriendStatusEnum {
    NORMAL(0, "正常"),
    BAN(1, "禁用"),
    ;


    private final Integer status;
    private final String desc;

    private static final Map<Integer, RoomFriendStatusEnum> CACHE;


    static {
        CACHE = Arrays.stream(RoomFriendStatusEnum.values()).collect(Collectors.toMap(RoomFriendStatusEnum::getStatus, Function.identity()));
    }

    public static RoomFriendStatusEnum of(Integer type) {
        return CACHE.get(type);
    }
}
