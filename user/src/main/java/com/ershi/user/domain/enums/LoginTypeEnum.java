package com.ershi.user.domain.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 登录方式枚举
 *
 * @author Ershi-Gu.
 * @since 2025-09-03
 */
@Getter
public enum LoginTypeEnum {

    ACCOUNT("0", "账号登录"),
    EMAIL("1", "邮箱登录"),
    ;

    /**
     * 登录方式类型
     */
    private final String type;
    /**
     * 中文描述
     */
    private final String desc;

    LoginTypeEnum(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    /**
     * 缓存枚举实体类
     */
    private static final Map<String, LoginTypeEnum> CACHE;

    static {
        CACHE = Arrays.stream(LoginTypeEnum.values())
                .collect(Collectors.toMap(LoginTypeEnum::getType, Function.identity()));
    }

    /**
     * 获取枚举实例
     *
     * @param type 数据源类型
     * @return {@link LoginTypeEnum }
     */
    public static LoginTypeEnum getEnumByType(String type) {
        return CACHE.get(type);
    }
}
