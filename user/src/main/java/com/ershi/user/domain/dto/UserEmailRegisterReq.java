package com.ershi.user.domain.dto;

/**
 * 用户注册请求体
 *
 * @author Ershi-Gu.
 * @since 2025-09-05
 */
public class UserEmailRegisterReq {

    /**
     * 账号
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

    // todo 完善验证码
}
