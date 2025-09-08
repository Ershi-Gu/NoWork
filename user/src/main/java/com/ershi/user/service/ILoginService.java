package com.ershi.user.service;

import com.ershi.user.domain.dto.UserLoginReq;
import com.ershi.user.domain.vo.UserLoginVO;

/**
 * 登录统一接口
 *
 * @author Ershi-Gu.
 * @since 2025-09-08
 */
public interface ILoginService {

    /**
     * 登录
     *
     * @param userLoginReq
     * @return {@link UserLoginVO }
     */
    UserLoginVO login(UserLoginReq userLoginReq);

    /**
     * 注销
     *
     * @param token
     */
    void logout(String token);
}
