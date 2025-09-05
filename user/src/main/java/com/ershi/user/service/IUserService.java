package com.ershi.user.service;


import com.ershi.user.domain.dto.UserEmailCaptchaReq;
import com.ershi.user.domain.entity.UserEntity;
import com.mybatisflex.core.service.IService;

/**
 * 用户表 服务层。
 *
 * @author mybatis-flex-helper automatic generation
 * @since 1.0
 */
public interface IUserService extends IService<UserEntity> {

    /**
     * 请求发送邮箱验证码
     *
     * @param email
     */
    void sendEmailCaptcha(String email);
}