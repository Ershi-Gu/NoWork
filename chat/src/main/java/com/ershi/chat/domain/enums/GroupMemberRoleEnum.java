package com.ershi.chat.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * 群组成员权限枚举
 *
 * @author Ershi
 * @date 2025/02/17
 */
@AllArgsConstructor
@Getter
public enum GroupMemberRoleEnum {

    LEADER(2, "群主"),
    MANAGER(1, "管理"),
    MEMBER(0, "普通成员"),
    REMOVE(-1, "被移除的成员"),
    ;

    private final Integer type;
    private final String desc;

    private static final Map<Integer, GroupMemberRoleEnum> CACHE;

    public static final List<Integer> ADMIN_LIST = Arrays.asList(LEADER.getType(), MANAGER.getType());

    static {
        CACHE = Arrays.stream(GroupMemberRoleEnum.values()).collect(Collectors.toMap(GroupMemberRoleEnum::getType, Function.identity()));
    }

    public static GroupMemberRoleEnum of(Integer type) {
        return CACHE.get(type);
    }
}
