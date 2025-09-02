package com.ershi.common.utils;

import com.ershi.common.domain.RequestInfo;

/**
 * 持有 web 请求信息上下文
 *
 * @author Ershi
 * @date 2024/12/04
 */
public class RequestHolder {

    private static final ThreadLocal<RequestInfo> THREAD_LOCAL = new ThreadLocal<>();

    public static void set(RequestInfo requestInfo) {
        THREAD_LOCAL.set(requestInfo);
    }

    public static RequestInfo get() {
        return THREAD_LOCAL.get();
    }

    public static void remove() {
        THREAD_LOCAL.remove();
    }
}
