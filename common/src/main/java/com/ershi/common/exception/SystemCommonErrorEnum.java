package com.ershi.common.exception;

import lombok.AllArgsConstructor;

/**
 * 系统通用异常枚举
 * @author Ershi
 * @date 2024/12/05
 */
@AllArgsConstructor
public enum SystemCommonErrorEnum implements ErrorEnum {

    LOCK_LIMIT(42900, "请求太频繁了，请稍后再试"),
    SYSTEM_ERROR(50000, "系统异常，请稍后再试"),
    ;

    /**
     * 异常码
     */
    private final Integer code;
    /**
     * 异常信息
     */
    private final String msg;

    @Override
    public Integer getErrorCode() {
        return this.code;
    }

    @Override
    public String getErrorMsg() {
        return this.msg;
    }
}
