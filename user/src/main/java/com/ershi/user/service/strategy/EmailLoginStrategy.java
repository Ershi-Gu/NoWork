package com.ershi.user.service.strategy;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import com.ershi.common.exception.BusinessErrorEnum;
import com.ershi.common.utils.AssertUtil;
import com.ershi.user.domain.dto.UserLoginReq;
import com.ershi.user.domain.entity.UserEntity;
import com.ershi.user.domain.enums.LoginTypeEnum;
import com.ershi.user.domain.vo.UserLoginVO;
import com.ershi.user.manager.CaptchaVerifyManager;
import com.ershi.user.mapper.UserMapper;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import static com.ershi.user.domain.entity.table.UserEntityTableDef.USER_ENTITY;

/**
 * 邮箱登录
 *
 * @author Ershi-Gu.
 * @since 2025-09-08
 */
@Component
public class EmailLoginStrategy implements ILoginStrategy{

    @Resource
    private UserMapper userMapper;

    @Resource
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Resource
    private CaptchaVerifyManager captchaVerifyManager;

    @Override
    public LoginTypeEnum getLoginType() {
        return LoginTypeEnum.EMAIL;
    }

    @Override
    public UserLoginVO login(UserLoginReq userLoginReq) {
        String email = userLoginReq.getIdentifier();
        String password = userLoginReq.getCredential();
        AssertUtil.isFalse(StringUtils.isAnyBlank(email, password),
                BusinessErrorEnum.API_PARAM_ERROR, "请输入账号密码");

        // 二次校验验证码
        String captchaVerification = userLoginReq.getCaptchaVerification();
        AssertUtil.isNotBlank(captchaVerification, BusinessErrorEnum.CAPTCHA_ERROR);
        AssertUtil.isTrue(captchaVerifyManager.verify(captchaVerification), BusinessErrorEnum.USER_LOGIN_ERROR);

        // 判断邮箱是否存在
        UserEntity user = userMapper.selectOneByQuery(QueryWrapper.create().where(USER_ENTITY.EMAIL.eq(email)));
        AssertUtil.nonNull(user, BusinessErrorEnum.USER_LOGIN_ERROR);

        // 校验密码
        boolean pwdCheck = bCryptPasswordEncoder.matches(password, user.getPassword());
        AssertUtil.isTrue(pwdCheck, BusinessErrorEnum.USER_LOGIN_ERROR);

        // 登录
        StpUtil.login(user.getId());

        // 构造返回
        UserLoginVO userLoginVO = new UserLoginVO();
        BeanUtil.copyProperties(user, userLoginVO);
        userLoginVO.setToken(StpUtil.getTokenValue());

        return userLoginVO;
    }
}
