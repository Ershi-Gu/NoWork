package com.ershi.common.config;

import com.ershi.common.interceptor.CollectorInterceptor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 拦截器配置类
 *
 * @author Ershi
 * @date 2024/12/04
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Resource
    private CollectorInterceptor collectorInterceptor;

    /**
     * 添加拦截器
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 信息收集拦截器
        registry.addInterceptor(collectorInterceptor)
                .addPathPatterns("/**");
    }
}
