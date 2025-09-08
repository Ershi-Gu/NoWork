package com.ershi.user.service.strategy;

import com.ershi.user.domain.dto.UserLoginReq;
import com.ershi.user.domain.enums.LoginTypeEnum;
import com.ershi.user.domain.vo.UserLoginVO;

/**
 * 登录策略接口，新增登录方式需要实现该接口
 *
 * @author Ershi-Gu.
 * @since 2025-09-08
 */
public interface ILoginStrategy {

    /**
     * 获取当前登录方式
     *
     * @return {@link LoginTypeEnum }
     */
    LoginTypeEnum getLoginType();

    /**
     * 登录实现
     *
     * @param userLoginReq
     * @return {@link UserLoginVO }
     */
    UserLoginVO login(UserLoginReq userLoginReq);
}
