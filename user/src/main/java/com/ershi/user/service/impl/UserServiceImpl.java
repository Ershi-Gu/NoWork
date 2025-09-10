package com.ershi.user.service.impl;


import cn.dev33.satoken.stp.StpUtil;
import com.ershi.common.constants.RedisKey;
import com.ershi.common.exception.BusinessErrorEnum;
import com.ershi.common.exception.SystemCommonErrorEnum;
import com.ershi.common.manager.RedissonManager;
import com.ershi.common.utils.AssertUtil;
import com.ershi.common.utils.RedisUtils;
import com.ershi.common.utils.RequestHolder;
import com.ershi.user.domain.dto.UserEmailRegisterReq;
import com.ershi.user.domain.dto.UserLoginReq;
import com.ershi.user.domain.entity.UserEntity;
import com.ershi.user.domain.vo.UserLoginVO;
import com.ershi.user.manager.CaptchaManager;
import com.ershi.user.manager.EmailManager;
import com.ershi.user.mapper.UserMapper;
import com.ershi.user.service.ILoginService;
import com.ershi.user.service.IUserService;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.redisson.api.RateIntervalUnit;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.ershi.user.domain.entity.table.UserEntityTableDef.USER_ENTITY;


/**
 * 用户表 服务层实现。
 *
 * @author mybatis-flex-helper automatic generation
 * @since 1.0
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements IUserService {

    @Resource
    private EmailManager emailManager;

    @Resource
    private RedissonManager redissonManager;

    @Resource
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Resource
    private ILoginService loginService;

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

    @Override
    public String emailRegister(UserEmailRegisterReq userEmailRegisterReq) {
        String account = userEmailRegisterReq.getAccount();
        String password = userEmailRegisterReq.getPassword();
        String checkPassword = userEmailRegisterReq.getCheckPassword();
        String email = userEmailRegisterReq.getEmail();
        String emailCaptcha = userEmailRegisterReq.getEmailCaptcha();

        // 验证参数
        AssertUtil.isFalse(StringUtils.isAnyBlank(password, checkPassword, email, emailCaptcha),
                BusinessErrorEnum.API_PARAM_ERROR, "参数为空异常");

        // 密码验证
        AssertUtil.isTrue(password.equals(checkPassword),
                BusinessErrorEnum.API_PARAM_ERROR, "密码不一致");

        // 验证邮箱是否已存在
        boolean exist = this.count(QueryWrapper.create().where(USER_ENTITY.EMAIL.eq(email))) > 0;
        AssertUtil.isFalse(exist, BusinessErrorEnum.EMAIL_EXIST_ERROR);

        // 校验邮箱验证码
        String captchaRedisKey = RedisKey.getKey(RedisKey.REGISTER_EMAIL_CAPTCHA_KEY, email);
        String captcha = RedisUtils.get(captchaRedisKey, String.class);
        AssertUtil.equal(emailCaptcha, captcha, BusinessErrorEnum.EMAIL_CAPTCHA_ERROR);
        // 校验完毕后删除Redis中的验证码，防止重复注册
        RedisUtils.del(captchaRedisKey);

        // 用户数据2DB
        String encodedPassword = bCryptPasswordEncoder.encode(password);
        UserEntity registerUser = new UserEntity();
        registerUser.setAccount(account != null ? account : "邮箱注册用户:" + email);
        registerUser.setPassword(encodedPassword);
        registerUser.setName("快取个名字吧~");
        registerUser.setEmail(email);
        boolean save = this.save(registerUser);

        AssertUtil.isTrue(save, SystemCommonErrorEnum.DB_ERROR, "注册服务异常，请稍后再试");

        // 获取token返回，前端可用于注册后直接登录
        StpUtil.login(registerUser.getId());
        return StpUtil.getTokenValue();
    }

    @Override
    public UserLoginVO login(UserLoginReq userLoginReq) {
        // 参数验证
        String type = userLoginReq.getType();
        String identifier = userLoginReq.getIdentifier();
        String credential = userLoginReq.getCredential();
        AssertUtil.isFalse(StringUtils.isAnyBlank(type, identifier, credential), BusinessErrorEnum.USER_LOGIN_ERROR);

        return loginService.login(userLoginReq);
    }

    @Override
    public Long getUidByAccount(String targetAccount) {
        UserEntity user = this.getOne(QueryWrapper.create().where(USER_ENTITY.ACCOUNT.eq(targetAccount)));
        return user != null ? user.getId() : null;
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