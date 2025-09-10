package com.ershi.common.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import com.ershi.common.interceptor.CollectorInterceptor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
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
     * cookie和跨域配置
     *
     * @param registry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                // 允许携带 Cookie
                .allowCredentials(true)
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("*");
    }

    /**
     * 添加拦截器
     *
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 信息收集拦截器
        registry.addInterceptor(collectorInterceptor)
                .addPathPatterns("/**");
        // 注册 Sa-Token 拦截器，打开注解式鉴权功能
        registry.addInterceptor(new SaInterceptor()).addPathPatterns("/**");
    }
}
