package com.ershi.user.domain.dto;

import lombok.Getter;

/**
 * 登录请求
 *
 * @author Ershi-Gu.
 * @since 2025-09-08
 */
@Getter
public class UserLoginReq {

    /**
     * 登录方式：account / email / wechat ...
     */
    private String type;

    /**
     * 登录标识（账号/邮箱/微信openId等）
     */
    private String identifier;

    /**
     * 登录凭证（密码/临时code等）
     */
    private String credential;

    /**
     * 滑块验证码验证数据，aes加密，数据在前端success函数回调参数中获取
     */
    private String captchaVerification;
}
