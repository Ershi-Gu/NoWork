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
    EMAIL_ERROR(19997, "邮箱服务异常"),
    API_PARAM_ERROR(19996, "接口参数错误"),
    EMAIL_EXIST_ERROR(19995, "该邮箱已注册"),
    EMAIL_CAPTCHA_ERROR(19994, "邮箱验证码错误"),
    USER_LOGIN_ERROR(19993, "登录失败，请检查账号密码"),
    LOGIN_STRATEGY_ERROR(19992, "登录方式错误"),
    CAPTCHA_ERROR(19991, "验证码错误"),
    USER_NOT_EXIST_ERROR(19990, "用户不存在"),
    FRIEND_EXIST_ERROR(19989, "好友重复添加"),
    APPLY_NOT_EXIST_ERROR(19988, "申请记录不存在"),
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
