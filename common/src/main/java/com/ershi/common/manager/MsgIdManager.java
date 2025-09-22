package com.ershi.common.manager;

import com.ershi.common.constants.RedisKey;
import com.ershi.common.exception.BusinessException;
import com.ershi.common.exception.SystemCommonErrorEnum;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.locks.ReentrantLock;

/**
 * 消息自增唯一id生成器
 *
 * @author Ershi-Gu.
 * @since 2025-09-22
 */
@Component
public class MsgIdManager {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    // todo 修改为注解式分布式锁
    private final ReentrantLock lock = new ReentrantLock();

    /**
     * 本地缓存区间
     */
    private long startId = 0;
    private long endId = -1;

    /**
     * 批量申请区间大小
     */
    private final int batchSize = 1000;

    /**
     * 获取下一个唯一ID <br>
     * 该方法通过Redis实现分布式ID生成，采用批量预分配的方式提高性能。 <br>
     * 本地维护一个ID区间[startId, endId]，当区间用完时从Redis申请新的批次。
     *
     * @return long 返回下一个可用的唯一ID
     */
    public long nextId() {
        lock.lock();
        try {
            // 检查本地ID区间是否已用完
            if (startId > endId) {
                // 本地区间用完 → 从 Redis 申请下一批
                Long newEndId = stringRedisTemplate.opsForValue()
                        .increment(RedisKey.getKey(RedisKey.MSG_ID_KEY), batchSize);
                if (newEndId == null) {
                    throw new BusinessException(SystemCommonErrorEnum.REDIS_ERROR.getErrorCode(), "Redis INCR failed");
                }
                startId = newEndId - batchSize + 1;
                endId = newEndId;
            }
            return startId++;
        } finally {
            lock.unlock();
        }
    }


    /**
     * 当Redis重启或崩溃时，可从db回复最后获取到的msgId，保证消息时序性。
     *
     * @param maxMsgId
     */
    public void initMsgId(Long maxMsgId) {
        stringRedisTemplate.opsForValue().set(RedisKey.MSG_ID_KEY, String.valueOf(maxMsgId));
    }

}
