package com.ershi.transaction.service;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.ershi.common.utils.JsonUtils;
import com.ershi.transaction.dao.SecureInvokeRecordDao;
import com.ershi.transaction.domain.entity.SecureInvokeRecordEntity;
import com.ershi.transaction.domain.dto.SecureInvokeDTO;
import com.ershi.transaction.utils.SecureInvokeHolder;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

/**
 * 方法安全调用执行器
 * @author Ershi
 * @date 2025/02/06
 */
@Slf4j
@AllArgsConstructor
public class SecureInvokeService {

    /**
     * 重试间隔时间，至少要入库2分钟
     */
    public static final double RETRY_INTERVAL_MINUTES = 2D;

    /**
     * 最大重试间隔时间
     */
    public static final double MAX_WAIT_MINUTES = 60 * 24;

    /**
     * 本地事务表db操作服务类
     */
    private final SecureInvokeRecordDao secureInvokeRecordDao;

    /**
     * 异步执行安全调用所用到的线程池
     */
    private final Executor executor;

    /**
     * 重试方法，每5秒查询一次本地事务表，并进行异步重试
     */
    @Scheduled(cron = "*/5 * * * * ?")
    public void retry() {
        List<SecureInvokeRecordEntity> secureInvokeRecords = secureInvokeRecordDao.getWaitRetryRecords();
        for (SecureInvokeRecordEntity secureInvokeRecord : secureInvokeRecords) {
            doAsyncInvoke(secureInvokeRecord);
        }
    }

    /**
     * 安全调用方法主体
     * @param record
     * @param async
     */
    public void invoke(SecureInvokeRecordEntity record, boolean async) {
        boolean inTransaction = TransactionSynchronizationManager.isActualTransactionActive();
        // 判断当前方法是否是事务状态，如果不是则直接执行，不做任何保证。
        if (!inTransaction) {
            return;
        }

        // 保存方法快找到本地事务表 -> 该操作与外部使用@SecureInvoke注解的方法在同一个事务，因此可以保障保存成功
        secureInvokeRecordDao.save(record);

        // 注册一个事务钩子函数，在事务提交后立马执行一次调用，不需要等待定时任务拉取重试
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @SneakyThrows
            @Override
            public void afterCommit() {
                // 事务后执行
                if (async) {
                    doAsyncInvoke(record);
                } else {
                    doInvoke(record);
                }
            }
        });
    }

    /**
     * 异步执行方法
     * @param record
     */
    public void doAsyncInvoke(SecureInvokeRecordEntity record) {
        executor.execute(() -> {
//            log.info(Thread.currentThread().getName());
            doInvoke(record);
        });
    }

    /**
     * 同步执行方法
     * @param record
     */
    public void doInvoke(SecureInvokeRecordEntity record) {
        // 获取请求快照参数
        SecureInvokeDTO secureInvokeDTO = record.getSecureInvokeJson();
        try {
            SecureInvokeHolder.setInvoking();
            Class<?> beanClass = Class.forName(secureInvokeDTO.getClassName());
            Object bean = SpringUtil.getBean(beanClass);
            List<String> argsTypeStrings = JsonUtils.toList(secureInvokeDTO.getParameterTypes(), String.class);
            List<Class<?>> argsTypes = getArgsType(argsTypeStrings);
            Method method = ReflectUtil.getMethod(beanClass, secureInvokeDTO.getMethodName(), argsTypes.toArray(new Class[]{}));
            Object[] args = getArgs(secureInvokeDTO, argsTypes);

            // 执行快照方法
            method.invoke(bean, args);

            // 执行成功更新本地事务表
            removeRecord(record.getId());
        } catch (Throwable e) {
            log.error("SecureInvokeService invoke fail", e);
            // 执行失败，等待下次重试执行
            scheduleNextRetry(record, e.getMessage());
        } finally {
            SecureInvokeHolder.removeInvoked();
        }
    }

    /**
     * 计算下一次重试时间（指数退避 + 最大间隔限制）
     *
     * @param retryTimes 已重试次数
     * @return {@link LocalDateTime} 下一次重试时间
     */
    private LocalDateTime getNextRetryTime(Integer retryTimes) {
        // 指数退避时间（单位：分钟）
        double waitMinutes = Math.pow(RETRY_INTERVAL_MINUTES, retryTimes);

        // 设置最大重试间隔时间
        waitMinutes = Math.min(waitMinutes, MAX_WAIT_MINUTES);

        // 将分钟转换为秒
        long waitSeconds = (long) (waitMinutes * 60);

        // 当前时间加上等待秒数
        return LocalDateTime.now().plusSeconds(waitSeconds);
    }

    /**
     * 计划下一次重试，并更新本地事务记录的状态。
     *
     * <p>该方法用于在方法调用失败后：
     * <ul>
     *     <li>增加已重试次数</li>
     *     <li>记录本次失败原因</li>
     *     <li>计算并设置下一次重试时间（指数退避）</li>
     *     <li>如果已达到最大重试次数，则将状态标记为失败</li>
     * </ul>
     *
     * @param record   需要重试的本地事务记录实体
     * @param errorMsg 本次执行失败的异常信息，用于记录失败原因
     */
    private void scheduleNextRetry(SecureInvokeRecordEntity record, String errorMsg) {
        Integer retryTimes = record.getRetryTimes() + 1;
        SecureInvokeRecordEntity update = new SecureInvokeRecordEntity();
        update.setId(record.getId());
        update.setFailReason(errorMsg);
        update.setNextRetryTime(getNextRetryTime(retryTimes));
        if (retryTimes > record.getMaxRetryTimes()) {
            update.setStatus(SecureInvokeRecordEntity.STATUS_FAIL);
        } else {
            update.setRetryTimes(retryTimes);
        }
        secureInvokeRecordDao.updateById(update);
    }

    /**
     * 删除快照
     *
     * @param id
     */
    private void removeRecord(Long id) {
        secureInvokeRecordDao.removeById(id);
    }

    /**
     * 获取方法类型，String -> Class
     *
     *
     * @param argsTypeStrings
     * @return {@link List }<{@link Class }<{@link ? }>>
     */
    @NotNull
    private List<Class<?>> getArgsType(List<String> argsTypeStrings) {
        return argsTypeStrings.stream().map(name -> {
            try {
                return Class.forName(name);
            } catch (ClassNotFoundException e) {
                log.error("SecureInvokeService class not fund", e);
            }
            return null;
        }).collect(Collectors.toList());
    }

    /**
     * 获取方法参数，带类型
     *
     * @param secureInvokeDTO
     * @param parameterClasses
     * @return {@link Object[] }
     */
    @NotNull
    private Object[] getArgs(SecureInvokeDTO secureInvokeDTO, List<Class<?>> parameterClasses) {
        // 使用 fastjson2 解析 JSON 数组字符串
        JSONArray jsonArray = JSON.parseArray(secureInvokeDTO.getArgs());

        Object[] args = new Object[jsonArray.size()];
        for (int i = 0; i < jsonArray.size(); i++) {
            Class<?> aClass = parameterClasses.get(i);
            args[i] = jsonArray.getObject(i, aClass);
        }
        return args;
    }
}
