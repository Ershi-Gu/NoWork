package com.ershi.transaction.service.adapter;

import com.ershi.common.utils.JsonUtils;
import com.ershi.transaction.annotation.SecureInvoke;
import com.ershi.transaction.domain.dto.SecureInvokeDTO;
import com.ershi.transaction.domain.entity.SecureInvokeRecordEntity;
import com.ershi.transaction.service.SecureInvokeService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 安全调用快照信息处理器
 *
 * @author Ershi
 * @date 2025/02/06
 */
public class SecureInvokeAdapter {

    /**
     * 构建安全调用方法快照信息
     *
     * @param joinPoint
     * @param secureInvoke
     * @return {@link SecureInvokeRecordEntity }
     */
    public static SecureInvokeRecordEntity buildSecureInvokeRecord(ProceedingJoinPoint joinPoint, SecureInvoke secureInvoke) {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        List<String> parameters = Stream.of(method.getParameterTypes()).map(Class::getName).collect(Collectors.toList());
        SecureInvokeDTO dto = SecureInvokeDTO.builder()
                .args(JsonUtils.toStr(joinPoint.getArgs()))
                .className(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameterTypes(JsonUtils.toStr(parameters))
                .build();
        return SecureInvokeRecordEntity.builder()
                .secureInvokeJson(dto)
                .status(SecureInvokeRecordEntity.STATUS_WAIT)
                .retryTimes(0)
                .maxRetryTimes(secureInvoke.maxRetryTimes())
                .nextRetryTime(LocalDateTime.now().plusMinutes((long) SecureInvokeService.RETRY_INTERVAL_MINUTES))
                .build();
    }
}
