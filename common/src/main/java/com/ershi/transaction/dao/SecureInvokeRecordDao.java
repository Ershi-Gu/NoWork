package com.ershi.transaction.dao;

import com.ershi.transaction.domain.entity.SecureInvokeRecordEntity;
import com.ershi.transaction.mapper.SecureInvokeRecordMapper;
import com.ershi.transaction.service.SecureInvokeService;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

import static com.ershi.transaction.domain.entity.table.SecureInvokeRecordEntityTableDef.SECURE_INVOKE_RECORD_ENTITY;


/**
 *
 * @author <a href="https://github.com/Ershi-Gu">Ershi</a>
 * @since 2025-02-06
 */
@Component
public class SecureInvokeRecordDao {

    @Resource
    private SecureInvokeRecordMapper mapper;

    /**
     * 查询入库至少两分钟的需要重试的方法记录
     */
    public List<SecureInvokeRecordEntity> getWaitRetryRecords() {
        // 当前时间
        LocalDateTime now = LocalDateTime.now();

        // 计算安全延迟时间：RETRY_INTERVAL_MINUTES 分钟前
        LocalDateTime afterTime = now.minusMinutes((long) SecureInvokeService.RETRY_INTERVAL_MINUTES);

        // 查询等待重试记录
        return mapper.selectListByQuery(QueryWrapper.create()
                .where(SECURE_INVOKE_RECORD_ENTITY.STATUS.eq(SecureInvokeRecordEntity.STATUS_WAIT)
                        .and(SECURE_INVOKE_RECORD_ENTITY.NEXT_RETRY_TIME.lt(now))
                        .and(SECURE_INVOKE_RECORD_ENTITY.CREATE_TIME.lt(afterTime))
                )
        );
    }

    /**
     * 保存入库
     *
     * @param secureInvokeRecordEntity
     */
    public void save(SecureInvokeRecordEntity secureInvokeRecordEntity) {
        mapper.insertSelective(secureInvokeRecordEntity);
    }

    /**
     * 删除快照
     *
     * @param id
     */
    public void removeById(Long id) {
        mapper.deleteById(id);
    }

    public void updateById(SecureInvokeRecordEntity update) {
        mapper.update(update);
    }
}
