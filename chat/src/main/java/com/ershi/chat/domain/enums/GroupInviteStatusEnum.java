package com.ershi.chat.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 群聊邀请状态枚举
 *
 * @author Ershi_Gu
 * @since 2025/10/10
 */
@AllArgsConstructor
@Getter
public enum GroupInviteStatusEnum {

    PENDING(0, "待确认"),
    ACCEPTED(1, "已接受"),
    REJECTED(2, "已拒绝");

    private final Integer type;

    private final String desc;

    /**
     * 枚举类型缓存
     */
    private static final Map<Integer, GroupInviteStatusEnum> CACHE;

    static {
        CACHE = Arrays.stream(GroupInviteStatusEnum.values()).collect(Collectors.toMap(GroupInviteStatusEnum::getType, Function.identity()));
    }

    /**
     * 获取枚举类
     *
     * @param type 状态类型
     * @return {@link GroupInviteStatusEnum}
     */
    public static GroupInviteStatusEnum of(Integer type) {
        return CACHE.get(type);
    }
}