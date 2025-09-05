package com.ershi.user.manager;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

/**
 * 验证相关
 *
 * @author Ershi-Gu.
 * @since 2025-09-05
 */
public class CaptchaManager {

    /**
     * 用于生成随机数字验证码
     */
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    /**
     * 生成6位数字验证码，用于邮箱验证
     *
     * @return {@link String }
     */
    public static String generateEmailCaptcha() {
        return String.valueOf(100000 + SECURE_RANDOM.nextInt(900000));
    }
}
