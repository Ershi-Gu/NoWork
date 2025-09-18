package com.ershi.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池配置
 *
 * @author Ershi
 * @date 2024/11/29
 */
@Configuration
@EnableAsync
public class ThreadPoolConfig implements AsyncConfigurer {

    /**
     * 项目通用线程池
     */
    public static final String NO_WORK_EXECUTOR = "noWorkExecutor";
    /**
     * websocket通信线程池
     */
    public static final String WS_EXECUTOR = "websocketExecutor";
    /**
     * websocket虚拟线程Executor
     */
    public static final String WS_VIRTUAL_EXECUTOR = "wsVirtualExecutor";

    /**
     * 指定@Async使用的线程池
     *
     * @return {@link Executor}
     */
    @Override
    public Executor getAsyncExecutor() {
        return noWorkExecutor();
    }

    /**
     * 自定义项目通用线程池
     *
     * @return {@link ThreadPoolTaskExecutor}
     */
    @Bean(NO_WORK_EXECUTOR)
    @Primary
    public ThreadPoolTaskExecutor noWorkExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(200);
        executor.setThreadNamePrefix("noWork-executor-");
        // 满了调用线程执行，认为重要任务
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 优雅停机 => spring自带，线程池关闭时，等待任务执行完毕再关闭
        executor.setWaitForTasksToCompleteOnShutdown(true);
        // 自定义线程工厂，内置线程异常处理器
        executor.setThreadFactory(new MyThreadFactory(executor));
        executor.initialize();
        return executor;
    }

    /**
     * websocket通信共用线程池
     *
     * @return {@link ThreadPoolTaskExecutor}
     */
    @Bean(WS_EXECUTOR)
    public ThreadPoolTaskExecutor websocketExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(16);
        executor.setMaxPoolSize(16);
        //支持同时推送1000人
        executor.setQueueCapacity(1000);
        executor.setThreadNamePrefix("ws-executor-");
        //满了直接丢弃，默认为不重要消息推送
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
        executor.setThreadFactory(new MyThreadFactory(executor));
        executor.initialize();
        return executor;
    }

    /**
     * websocket通信虚拟线程池，该线程池并不作池化，由于虚拟线程属于非常轻量级的资源，因此，用时创建，用完就扔，不要池化虚拟线程。
     *
     * @return {@link Executor}
     */
    @Bean(WS_VIRTUAL_EXECUTOR)
    public Executor websocketVirtualExecutor() {
        // 虚拟线程的基础线程工厂
        ThreadFactory virtualFactory = Thread.ofVirtual()
                .name("ws-virtual-", 0)
                .factory();
        // 自定义线程工厂包装基础工厂，实现线程内的异常处理
        ThreadFactory myFactory = new MyThreadFactory(virtualFactory);
        return Executors.newThreadPerTaskExecutor(myFactory);
    }
}
