package com.ershi.common.exception;

/**
 * 顶层异常类规范接口
 *
 * @author Ershi
 * @date 2024/12/04
 */
public interface ErrorEnum {

    /**
     * 获取错误码
     *
     * @return {@link Integer }
     */
    Integer getErrorCode();

    /**
     * 获取错误信息
     *
     * @return {@link String }
     */
    String getErrorMsg();
}
