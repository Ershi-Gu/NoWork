package com.ershi.common.aop;

import cn.hutool.core.date.StopWatch;
import cn.hutool.json.JSONUtil;
import com.ershi.common.domain.RequestInfo;
import com.ershi.common.utils.RedisIdWorker;
import com.ershi.common.utils.RequestHolder;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 请求日志记录拦截器，记录请求来源/响应时间/参数等
 *
 * @author Ershi-Gu.
 * @since 2025-09-02
 */
@Aspect
@Component
@Slf4j
public class LogAspect {

    @Resource
    private RedisIdWorker redisIdWorker;

    /**
     * 拦截所有api进行请求记录
     *
     * @param joinPoint
     * @return {@link Object }
     * @throws Throwable
     */
    @Around("execution(* com..controller..*.*(..))")
    public Object doInterceptor(ProceedingJoinPoint joinPoint) throws Throwable {
        // 计时
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        // 生成唯一请求 id78
        String requestId = redisIdWorker.nextId();
        // 获取请求信息
        HttpServletRequest request = ((ServletRequestAttributes)Objects.requireNonNull(
            RequestContextHolder.getRequestAttributes())).getRequest();
        String method = request.getMethod();
        String uri = request.getRequestURI();
        // 获取请求参数，如果参数有 HttpRequest,ServletResponse，移除不打印
        List<Object> paramList = Stream.of(joinPoint.getArgs()).filter(args -> !(args instanceof ServletRequest))
            .filter(args -> !(args instanceof ServletResponse)).collect(Collectors.toList());
        String printParamStr =
            paramList.size() == 1 ? JSONUtil.toJsonStr(paramList.get(0)) : JSONUtil.toJsonStr(paramList);
        // 获取请求用户信息
        RequestInfo requestInfo = RequestHolder.get();
        String userHeaderStr = JSONUtil.toJsonStr(requestInfo);
        if (log.isInfoEnabled()) {
            log.info("request start，id: {}, method: {}，uri: {}，userInfo: {}，param: {}", requestId, method, uri,
                userHeaderStr, printParamStr);
        }
        // 执行原方法
        Object result = joinPoint.proceed();
        // 统计耗时
        stopWatch.stop();
        long totalTimeMillis = stopWatch.getTotalTimeMillis();
        if (log.isInfoEnabled()) {
            log.info("request end, id: {}，cost: {}", requestId, totalTimeMillis);
        }
        return result;
    }
}
