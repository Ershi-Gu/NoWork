package com.ershi.chat.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 用户申请类别
 *
 * @author Ershi
 * @since 2025-09-09
 */
@AllArgsConstructor
@Getter
public enum UserApplyTypeEnum {

    FRIEND(1, "加好友"),
    GROUP(2, "加群聊"),
    ;

    private final Integer type;

    private final String desc;

    /**
     * 枚举类型缓存
     */
    private static final Map<Integer, UserApplyTypeEnum> CACHE;

    static {
        CACHE = Arrays.stream(UserApplyTypeEnum.values()).collect(Collectors.toMap(UserApplyTypeEnum::getType, Function.identity()));
    }

    /**
     * 获取枚举类
     *
     * @param type
     * @return {@link UserApplyTypeEnum}
     */
    public static UserApplyTypeEnum of(Integer type) {
        return CACHE.get(type);
    }
}
