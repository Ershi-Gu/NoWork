package com.ershi.user.service.impl;


import com.ershi.common.exception.BusinessErrorEnum;
import com.ershi.common.manager.RedissonManager;
import com.ershi.common.utils.AssertUtil;
import com.ershi.common.utils.RequestHolder;
import com.ershi.user.manager.CaptchaManager;
import com.ershi.user.manager.EmailManager;
import jakarta.annotation.Resource;
import org.apache.commons.validator.routines.EmailValidator;
import org.redisson.api.RateIntervalUnit;
import org.springframework.stereotype.Service;
import com.ershi.user.service.IUserService;
import com.ershi.user.domain.entity.UserEntity;
import com.ershi.user.mapper.UserMapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;

import java.util.List;

/**
 * 用户表 服务层实现。
 *
 * @author mybatis-flex-helper automatic generation
 * @since 1.0
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements IUserService {

    /**
     * 邮箱验证周期内允许请求次数
     */
    public static final long EMAIL_CAPTCHA_RATE = 1L;

    /**
     * 邮箱验证限流周期(s)
     */
    public static final long EMAIL_CAPTCHA_INTERVAL = 120L;

    /**
     * 邮箱格式验证器
     */
    private static final EmailValidator EMAIL_VALIDATOR = EmailValidator.getInstance();

    @Resource
    private EmailManager emailManager;

    @Resource
    private RedissonManager redissonManager;

    @Override
    public void sendEmailCaptcha(String email) {
        // 校验邮件格式
        validateEmail(email);

        // ip限流，120s内仅可请求一次
        redissonManager.doOverallRateLimit(RequestHolder.get().getIp(),
                EMAIL_CAPTCHA_RATE, EMAIL_CAPTCHA_INTERVAL, RateIntervalUnit.SECONDS);

        // 生成验证码，有效期5分钟
        String emailCaptcha = CaptchaManager.generateEmailCaptcha();

        // 发送邮件
        emailManager.sendRegisterEmail(email, emailCaptcha);
    }

    /**
     * 验证邮箱格式是否合法
     *
     * @param email
     */
    private void validateEmail(String email) {
        // 允许注册的邮箱地址
        List<String> allowedEmailList = List.of(
                "@qq.com",
                "@163.com",
                "@gmail.com",
                "@outlook.com",
                "@sohu.com"
        );

        AssertUtil.isNotBlank(email, BusinessErrorEnum.REGISTER_EMAIL_ERROR, "邮箱地址不能为空");
        // 获取邮箱后缀，从 @ 符号开始
        String suffix = email.substring(email.lastIndexOf("@"));
        AssertUtil.isTrue(allowedEmailList.contains(suffix),
                BusinessErrorEnum.REGISTER_EMAIL_ERROR, "该类型邮箱暂不支持注册，请更换邮箱");
        AssertUtil.isTrue(EMAIL_VALIDATOR.isValid(email),
                BusinessErrorEnum.REGISTER_EMAIL_ERROR, "邮箱地址格式错误");
    }
}