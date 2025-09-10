package com.ershi.common.exception;

import cn.dev33.satoken.exception.NotLoginException;
import com.ershi.common.domain.vo.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常捕获
 *
 * @author Ershi
 * @date 2024/12/05
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 未登录异常
     *
     * @param e
     * @return {@link ApiResult }<{@link Void }>
     */
    @ExceptionHandler(NotLoginException.class)
    public ApiResult<Void> notLoginExceptionHandler(NotLoginException e) {
        log.error("Not Login Exception！The reason is: {}", e.getMessage());
        return ApiResult.fail(BusinessErrorEnum.USER_NOT_LOGIN_ERROR);
    }

    /**
     * 业务异常处理
     *
     * @param e
     * @return {@link ApiResult}<{@link Void}>
     */
    @ExceptionHandler(BusinessException.class)
    public ApiResult<Void> businessExceptionHandler(BusinessException e) {
        log.error("Business Exception！The reason is: {}", e.getErrorMsg());
        return ApiResult.fail(e.getErrorCode(), e.getErrorMsg());
    }

    /**
     * 未知异常兜底
     *
     * @param e
     * @return {@link ApiResult}<{@link Void}>
     */
    @ExceptionHandler(Exception.class)
    public ApiResult<Void> systemExceptionHandler(Exception e) {
        log.error("System Exception! The reason is: {}", e.getMessage(), e);
        return ApiResult.fail(SystemCommonErrorEnum.SYSTEM_ERROR);
    }

}
