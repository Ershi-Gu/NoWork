package com.ershi.user.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.ershi.common.exception.BusinessErrorEnum;
import com.ershi.common.utils.AssertUtil;
import com.ershi.user.domain.dto.UserLoginReq;
import com.ershi.user.domain.vo.UserLoginVO;
import com.ershi.user.service.ILoginService;
import com.ershi.user.service.strategy.ILoginStrategy;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Ershi-Gu.
 * @since 2025-09-08
 */
@Service
public class LoginServiceImpl implements ILoginService {

    /**
     * 存放登录策略实例- type:strategy
     */
    private final Map<String, ILoginStrategy> strategyMap = new HashMap<>();

    public LoginServiceImpl(List<ILoginStrategy> loginStrategies) {
        loginStrategies.forEach(strategy -> {
            strategyMap.put(strategy.getLoginType().getType(), strategy);
        });
    }


    @Override
    public UserLoginVO login(UserLoginReq userLoginReq) {
        // 获取type对应的登录实例
        ILoginStrategy loginStrategy = strategyMap.get(userLoginReq.getType());
        AssertUtil.nonNull(loginStrategy, BusinessErrorEnum.LOGIN_STRATEGY_ERROR);
        return loginStrategy.login(userLoginReq);
    }

    @Override
    public void logout(String token) {
        StpUtil.logoutByTokenValue(token);
    }
}
