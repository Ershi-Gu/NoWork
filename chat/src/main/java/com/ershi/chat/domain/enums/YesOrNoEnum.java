package com.ershi.chat.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 是非判断
 * @author Ershi
 * @date 2025/02/08
 */
@AllArgsConstructor
@Getter
public enum YesOrNoEnum {

    NO(0, "否"),
    YES(1, "是"),
    ;

    private final Integer status;
    private final String desc;

    private static final Map<Integer, YesOrNoEnum> CACHE;

    static {
        CACHE = Arrays.stream(YesOrNoEnum.values()).collect(Collectors.toMap(YesOrNoEnum::getStatus, Function.identity()));
    }

    public static YesOrNoEnum of(Integer type) {
        return CACHE.get(type);
    }

    public static Integer toStatus(Boolean bool) {
        return bool ? YES.getStatus() : NO.getStatus();
    }
}
