package com.ershi.hotboard.domain.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 热榜分类枚举
 *
 * @author Ershi-Gu.
 * @since 2025-09-03
 */
@Getter
public enum HBCategoryEnum {

    COMPREHENSIVE_NEWS(0, "综合咨询"),
    ENTERTAINMENT(1, "娱乐"),
    CODE(2, "技术 & 编程"),
    MUSIC(3, "音乐");

    /**
     * 热榜分类
     */
    private final Integer type;
    /**
     * 热榜分类中文描述
     */
    private final String desc;

    HBCategoryEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    /**
     * 缓存枚举实体类
     */
    private static final Map<Integer, HBCategoryEnum> CACHE;

    static {
        CACHE = Arrays.stream(HBCategoryEnum.values())
                .collect(Collectors.toMap(HBCategoryEnum::getType, Function.identity()));
    }

    /**
     * 获取枚举实例
     *
     * @param type 数据源类型
     * @return {@link HBCategoryEnum }
     */
    public static HBCategoryEnum getEnumByType(Integer type) {
        return CACHE.get(type);
    }

    /**
     * 获取分类编码列表
     *
     * @return {@link List }<{@link String }>
     */
    public static List<Integer> getValues() {
        return new java.util.ArrayList<>(CACHE.keySet());
    }
}
