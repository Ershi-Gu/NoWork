package com.ershi.mainapp;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * 项目主启动类
 *
 * @author Ershi-Gu.
 * @since 2025-09-02
 */
@SpringBootApplication(scanBasePackages = {
        "com.ershi.hotboard"
})
@MapperScan(value = {
        "com.ershi.hotboard.mapper"
})
@EnableAspectJAutoProxy(exposeProxy = true)
public class MainAppApplication {


    public static void main(String[] args) {
        SpringApplication.run(MainAppApplication.class, args);
    }

}
