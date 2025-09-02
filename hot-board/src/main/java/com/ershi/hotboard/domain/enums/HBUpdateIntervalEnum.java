package com.ershi.hotboard.domain.enums;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 热榜更新时间间隔枚举
 *
 * @author Ershi-Gu.
 * @since 2025-09-03
 */
@Getter
public enum HBUpdateIntervalEnum {

    HALF_HOUR(BigDecimal.valueOf(0.5), "半小时"),
    ONE_HOUR(BigDecimal.valueOf(1.0), "一小时"),
    ;

    /**
     * 数据源类型
     */
    private final BigDecimal value;
    /**
     * 数据源类型中文描述
     */
    private final String desc;

    HBUpdateIntervalEnum(BigDecimal value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    /**
     * 缓存枚举实体类
     */
    private static final Map<BigDecimal, HBUpdateIntervalEnum> CACHE;

    static {
        CACHE = Arrays.stream(HBUpdateIntervalEnum.values())
                .collect(Collectors.toMap(HBUpdateIntervalEnum::getValue, Function.identity()));
    }

    /**
     * 获取枚举实例
     *
     * @param type 数据源类型
     * @return {@link HBUpdateIntervalEnum }
     */
    public static HBUpdateIntervalEnum getEnumByType(BigDecimal type) {
        return CACHE.get(type);
    }

    /**
     * 获取分类编码列表
     *
     * @return {@link List }<{@link String }>
     */
    public static List<BigDecimal> getValues() {
        return new java.util.ArrayList<>(CACHE.keySet());
    }
}
