package com.ershi.chat.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 用户申请记录已读未读
 *
 * @author Ershi
 * @since 2025-09-09
 */
@AllArgsConstructor
@Getter
public enum UserApplyReadStatusEnum {

    READ(0, "已读"),
    UNREAD(1, "未读"),
    ;

    private final Integer type;

    private final String desc;

    /**
     * 枚举类型缓存
     */
    private static final Map<Integer, UserApplyReadStatusEnum> CACHE;

    static {
        CACHE = Arrays.stream(UserApplyReadStatusEnum.values()).collect(Collectors.toMap(UserApplyReadStatusEnum::getType, Function.identity()));
    }

    /**
     * 获取枚举类
     *
     * @param type
     * @return {@link UserApplyReadStatusEnum}
     */
    public static UserApplyReadStatusEnum of(Integer type) {
        return CACHE.get(type);
    }
}
