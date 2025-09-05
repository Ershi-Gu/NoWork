package com.ershi.common.interceptor;


import com.ershi.common.domain.RequestInfo;
import com.ershi.common.utils.RequestHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 上下文信息收集器
 *
 * @author Ershi
 * @date 2024/12/04
 */
@Component
public class CollectorInterceptor implements HandlerInterceptor {

    /**
     * 请求前收集相关信息
     *
     * @param request
     * @param response
     * @param handler
     * @return boolean
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        RequestInfo requestInfo = RequestInfo.build(request);
        RequestHolder.set(requestInfo);
        return true;
    }

    /**
     * 请求结束后删除信息
     *
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        RequestHolder.remove();
    }
}
