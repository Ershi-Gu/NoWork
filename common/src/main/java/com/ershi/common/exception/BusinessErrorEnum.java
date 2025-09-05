package com.ershi.common.exception;

import lombok.AllArgsConstructor;

/**
 * 业务异常类型枚举
 *
 * @author Ershi
 * @date 2024/12/05
 */
@AllArgsConstructor
public enum BusinessErrorEnum implements ErrorEnum {
    COMMON_ERROR(19999, "业务异常"),
    REGISTER_EMAIL_ERROR(19998, "注册邮箱异常"),
    EMAIL_ERROR(19993, "邮箱服务异常"),
    ;

    private final Integer errorCode;

    private final String errorMsg;

    @Override
    public Integer getErrorCode() {
        return this.errorCode;
    }

    @Override
    public String getErrorMsg() {
        return this.errorMsg;
    }
}
