package com.ershi.chat.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 用户申请状态记录
 *
 * @author Ershi
 * @since 2025-09-09
 */
@AllArgsConstructor
@Getter
public enum UserApplyStatusEnum {

    PENDING(0, "待审批"),
    ACCEPTED(1, "已同意"),
    REJECTED(2, "已拒绝");

    private final Integer type;

    private final String desc;

    /**
     * 枚举类型缓存
     */
    private static final Map<Integer, UserApplyStatusEnum> CACHE;

    static {
        CACHE = Arrays.stream(UserApplyStatusEnum.values()).collect(Collectors.toMap(UserApplyStatusEnum::getType, Function.identity()));
    }

    /**
     * 获取枚举类
     *
     * @param type
     * @return {@link UserApplyStatusEnum}
     */
    public static UserApplyStatusEnum of(Integer type) {
        return CACHE.get(type);
    }
}
