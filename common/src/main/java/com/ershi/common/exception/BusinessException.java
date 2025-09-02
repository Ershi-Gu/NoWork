package com.ershi.common.exception;

import lombok.Getter;

/**
 * 业务异常
 *
 * @author Ershi
 * @since 2025-09-02
 */
@Getter
public class BusinessException extends RuntimeException {

    /**
     * 业务异常码
     */
    private final Integer errorCode;
    /**
     * 业务异常信息
     */
    private final String errorMsg;

    public BusinessException(Integer errorCode, String errorMsg) {
        super(errorMsg);
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public BusinessException(ErrorEnum errorEnum) {
        super(errorEnum.getErrorMsg());
        this.errorCode = errorEnum.getErrorCode();
        this.errorMsg = errorEnum.getErrorMsg();
    }
}
