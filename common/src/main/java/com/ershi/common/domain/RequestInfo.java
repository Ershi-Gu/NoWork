package com.ershi.common.domain;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.extra.servlet.JakartaServletUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Builder;
import lombok.Data;

import java.util.Optional;

/**
 * Web 请求相关信息
 *
 * @author Ershi
 * @since 2025-09-02
 */
@Data
@Builder
public class RequestInfo {

    /**
     * 唯一请求id
     */
    private String requestId;

    /**
     * 用户uid
     */
    private Long uid;

    /**
     * 用户ip
     */
    private String ip;

    /**
     * 根据request快速构造RequestInfo
     *
     * @param request
     * @return {@link RequestInfo }
     */
    public static RequestInfo build(HttpServletRequest request) {
        Long uid = Optional.ofNullable(StpUtil.getLoginIdDefaultNull())
                .map(o -> Long.valueOf(o.toString()))
                // 未登录时默认为null
                .orElse(null);

        return RequestInfo.builder()
                .uid(uid)
                .ip(JakartaServletUtil.getClientIP(request))
                .build();

    }
}
