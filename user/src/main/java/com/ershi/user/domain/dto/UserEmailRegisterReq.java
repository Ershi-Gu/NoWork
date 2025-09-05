package com.ershi.user.domain.dto;

import lombok.Data;
import lombok.Getter;

/**
 * 用户注册请求体
 *
 * @author Ershi-Gu.
 * @since 2025-09-05
 */
@Getter
public class UserEmailRegisterReq {

    /**
     * 账号（可选填）
     */
    private String account;

    /**
     * 密码
     */
    private String password;

    /**
     * 重复密码检查
     */
    private String checkPassword;

    /**
     * 注册邮箱
     */
    private String email;

    /**
     * 邮箱验证码
     */
    private String emailCaptcha;
}
