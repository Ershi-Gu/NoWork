package com.ershi.mainapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * 项目主启动类
 *
 * @author yunxiang.gu@hand-china.com
 * @since 2025-09-02
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.ershi.hotboard")
public class MainAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(MainAppApplication.class, args);
    }

}
