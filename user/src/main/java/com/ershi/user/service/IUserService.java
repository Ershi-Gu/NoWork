package com.ershi.user.service;


import com.ershi.user.domain.dto.UserEmailRegisterReq;
import com.ershi.user.domain.dto.UserLoginReq;
import com.ershi.user.domain.entity.UserEntity;
import com.ershi.user.domain.vo.UserLoginVO;
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

    /**
     * 邮箱注册
     *
     * @param userEmailRegisterReq
     * @return {@link String } 返回token用于前端自动登录
     */
    String emailRegister(UserEmailRegisterReq userEmailRegisterReq);

    /**
     * 登录，支持多种登录方式
     *
     * @param userLoginReq
     * @return {@link UserLoginVO }
     */
    UserLoginVO login(UserLoginReq userLoginReq);
}