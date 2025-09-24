package com.ershi.chat.job;

import com.ershi.chat.service.cache.MessageCache;
import com.ershi.chat.service.cache.MsgAckCache;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.Executor;

/**
 * 负责检查客户端接收消息确认情况，并提出重试任务
 *
 * @author Ershi-Gu.
 * @since 2025-09-23
 */
@Service
public class MsgAckJob {

    @Resource
    private MsgAckCache msgAckCache;

    @Resource
    private MessageCache messageCache;

    @Resource
    private Executor noWorkVirtualExecutor;

    // todo 查询客户端未ack消息，进行重发
    public void checkMsgAck() {
        // 获取超时未ack消息id
        Set<Long> expiredUnAckMsgIds = msgAckCache.getExpiredUnAckMsg();

        // 查询消息简表（redis），获取失败消息内容
//        messageCache.batchGetMsgFromCache()

        noWorkVirtualExecutor.execute(() -> {
            // 进行重试消息发送
        });
    }
}
