package com.ershi.common.domain;

import lombok.Builder;
import lombok.Data;

/**
 * Web 请求相关信息
 *
 * @author Ershi
 * @since 2025-09-02
 */
@Data
@Builder
public class RequestInfo {

    /** 唯一请求id */
    private String requestId;

    /**
     * 用户uid
     */
    private Long uid;

    /**
     * 用户ip
     */
    private String ip;
}
