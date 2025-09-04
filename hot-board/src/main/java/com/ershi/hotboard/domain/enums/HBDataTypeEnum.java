package com.ershi.hotboard.domain.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 热点数据源类型枚举
 *
 * @author Ershi-Gu.
 * @since 2025-09-03
 */
@Getter
public enum HBDataTypeEnum {

    BILI_BILI("BiliBili", "哔哩哔哩"),
    JUE_JIN("JueJin", "稀土掘金")
    ;

    /** 数据源类型 */
    private final String type;
    /** 数据源类型中文描述 */
    private final String desc;

    HBDataTypeEnum(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    /** 缓存枚举实体类 */
    private static final Map<String, HBDataTypeEnum> CACHE;

    static {
        CACHE = Arrays.stream(HBDataTypeEnum.values())
            .collect(Collectors.toMap(HBDataTypeEnum::getType, Function.identity()));
    }

    /**
     * 获取枚举实例
     *
     * @param type 数据源类型
     * @return {@link HBDataTypeEnum }
     */
    public static HBDataTypeEnum getEnumByType(String type) {
        return CACHE.get(type);
    }

    /**
     * 获取数据源类型列表
     *
     * @return {@link List }<{@link String }>
     */
    public static List<String> getValues() {
        return new java.util.ArrayList<>(CACHE.keySet());
    }
}
