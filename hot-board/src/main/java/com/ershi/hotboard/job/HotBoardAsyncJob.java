package com.ershi.hotboard.job;

import cn.hutool.core.date.StopWatch;
import com.ershi.common.utils.FutureUtils;
import com.ershi.hotboard.datasource.DataSource;
import com.ershi.hotboard.datasource.DataSourceFactory;
import com.ershi.hotboard.domain.entity.HotBoardEntity;
import com.ershi.hotboard.domain.enums.HBDataTypeEnum;
import com.ershi.hotboard.service.IHotBoardService;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.ershi.hotboard.domain.entity.table.HotBoardEntityTableDef.HOT_BOARD_ENTITY;

/**
 * 定时获取热榜数据增量到数据库-异步多线程
 *
 * @author Ershi-Gu.
 * @since 2025-09-03
 */
@Slf4j
@Component
public class HotBoardAsyncJob {

    @Resource
    private DataSourceFactory dataSourceFactory;

    @Resource
    private RetryTemplate retryTemplate;

    @Resource
    private ThreadPoolTaskExecutor noWorkExecutor;

    @Resource
    private IHotBoardService hotBoardService;

    /**
     * 每半小时执行一次任务-异步开启任务，为了等待所有线程结束并输出总处理时间
     */
    @Async
    @Scheduled(cron = "0 0/30 * * * ?")
    public void run() {
        log.info("热榜数据开始更新...");
        // 记录完整更新时间
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        // 获取所有数据源
        Collection<DataSource> allDataSources = dataSourceFactory.getAllDataSources();

        // 构建 CompletableFuture 列表
        List<CompletableFuture<Void>> futures = allDataSources.stream()
                .map(ds -> CompletableFuture.runAsync(() -> {
                    try {
                        retryTemplate.execute(ctx -> {
                            updateDataToDb(ds);
                            return null;
                        });
                    } catch (Exception e) {
                        log.error("数据源 [{}] 更新失败，已达到最大重试次数，放弃更新", ds.getTypeEnum().getDesc(), e);
                    }
                }, noWorkExecutor))
                .toList();

        // 等待所有任务线程执行完毕
        FutureUtils.sequenceNonNull(futures).join();

        stopWatch.stop();
        log.info("热榜数据更新完毕，耗时：{}ms", stopWatch.getTotalTimeMillis());
    }

    /**
     * 单个数据源更新数据到数据库
     *
     * @param dataSource
     */
    public void updateDataToDb(DataSource dataSource) {
        // 获取旧数据
        String type = dataSource.getTypeEnum().getType();
        QueryWrapper wrapper = QueryWrapper.create()
                .where(HOT_BOARD_ENTITY.TYPE.eq(type));

        HotBoardEntity oldData = hotBoardService.getOne(wrapper);
        // 判断是否满足更新间隔
        if (oldData != null) {
            // 小时转毫秒
            long updateIntervalMillis = oldData.getUpdateInterval()
                    .multiply(new BigDecimal(3600 * 1000)).longValue();
            long nextUpdateTime = oldData.getUpdateTime().getTime() + updateIntervalMillis;
            if (System.currentTimeMillis() < nextUpdateTime) {
                log.info("==================> 数据源 [{}] 更新时间间隔未到，跳过", dataSource.getTypeEnum().getDesc());
                return;
            }
        }

        // 获取新数据
        HotBoardEntity newData = dataSource.getHotBoardData();
        // 保留原id
        newData.setId(oldData != null ? oldData.getId() : null);

        hotBoardService.saveOrUpdate(newData);
        log.info("==================> 数据源 [{}] 加载完毕", dataSource.getTypeEnum().getDesc());
    }
}
