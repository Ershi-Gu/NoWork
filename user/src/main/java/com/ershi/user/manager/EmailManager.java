package com.ershi.user.manager;

import com.ershi.common.constants.RedisKey;
import com.ershi.common.exception.BusinessErrorEnum;
import com.ershi.common.exception.BusinessException;
import com.ershi.common.exception.SystemCommonErrorEnum;
import com.ershi.common.utils.RedisUtils;
import com.ershi.user.constants.EmailTemplate;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 邮件通知服务
 *
 * @author Ershi-Gu.
 * @since 2025-09-05
 */
@Slf4j
@Component
public class EmailManager {

    @Value("${spring.mail.username}")
    private String form;

    /**
     * 邮箱验证码过期时间
     */
    public static final int CAPTCHA_EXPIRE_TIME = 5 * 60;

    /**
     * 邮件发送客户端
     */
    private final JavaMailSender javaMailSender;

    public EmailManager(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    /**
     * 发送用户注册邮件
     *
     * @param email
     * @param captcha
     */
    @Async
    public void sendRegisterEmail(String email, String captcha) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            // 使用springboot封装的消息类
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            // 设置邮件标题
            messageHelper.setSubject("【挪窝】注册验证码");

            // 设置内容html
            String htmlContent = String.format(EmailTemplate.REGISTER_EMAIL_CAPTCHA_TEMPLATE, captcha);
            messageHelper.setText(htmlContent, true);

            // 设置发送邮箱-需要和配置中的username相同
            messageHelper.setFrom(form, "挪窝小站");
            // 设置目标邮箱
            messageHelper.setTo(email);

            // 发送
            javaMailSender.send(mimeMessage);

            // 保存验证码到Redis，用于后续验证
            RedisUtils.set(RedisKey.getKey(RedisKey.REGISTER_EMAIL_CAPTCHA_KEY, email), captcha, CAPTCHA_EXPIRE_TIME);

        } catch (Exception e) {
            log.error("注册邮件发送失败, email={}, captcha={}", email, captcha, e);
        }

    }
}
