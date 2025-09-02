package com.ershi.mainapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * 项目主启动类
 *
 * @author Ershi-Gu.
 * @since 2025-09-02
 */
@SpringBootApplication
@EnableAspectJAutoProxy(exposeProxy = true)
@ComponentScan(basePackages = {"com.ershi.hotboard", "com.ershi.common"})
public class MainAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(MainAppApplication.class, args);
    }

}
