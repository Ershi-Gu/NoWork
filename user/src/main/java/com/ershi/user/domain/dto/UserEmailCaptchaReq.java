package com.ershi.user.domain.dto;

import lombok.Getter;

/**
 * 请求发送邮箱验证码
 *
 * @author Ershi-Gu.
 * @since 2025-09-05
 */
@Getter
public class UserEmailCaptchaReq {

    /**
     * 邮箱地址
     */
    private String email;
}
