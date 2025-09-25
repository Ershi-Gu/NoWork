package com.ershi.chat.job;

import cn.hutool.core.date.StopWatch;
import com.ershi.chat.constants.MQConstant;
import com.ershi.chat.domain.dto.RetryMessage;
import com.ershi.chat.domain.message.MessageEntity;
import com.ershi.chat.service.cache.MessageCache;
import com.ershi.chat.service.cache.MsgAckCache;
import com.ershi.common.manager.MQProducer;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 负责检查客户端接收消息确认情况，并提出重试任务
 *
 * @author Ershi-Gu.
 * @since 2025-09-23
 */
@Slf4j
@Service
public class MsgAckJob {

    @Resource
    private MsgAckCache msgAckCache;

    @Resource
    private MessageCache messageCache;

    @Resource
    private MQProducer mqProducer;

    /**
     * 扫描未ack的消息，并重试发送一次，2s扫描一轮
     */
    @Scheduled(fixedRate = 2000)
    public void checkMsgAck() {
        // 获取每个 uid 对应的超时消息id
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Map<Long, Set<Long>> expiredUnAckMsg = msgAckCache.getExpiredUnAckMsg();
        if (expiredUnAckMsg.isEmpty()) {
            return;
        }

        // 聚合msgId
        Set<Long> allExpiredMsgIds = expiredUnAckMsg.values().stream()
                .flatMap(Set::stream)
                .collect(Collectors.toSet());

        // 查询消息简表（redis），获取失败消息内容
        List<MessageEntity> messageEntities = messageCache
                .batchGetMsgFromCache(allExpiredMsgIds.stream().toList());

        // 将消息分组：msgId -> receiverUids
        Map<Long, List<Long>> msgIdToReceivers = new HashMap<>();
        for (Map.Entry<Long, Set<Long>> entry : expiredUnAckMsg.entrySet()) {
            Long uid = entry.getKey();
            for (Long msgId : entry.getValue()) {
                msgIdToReceivers.computeIfAbsent(msgId, k -> new ArrayList<>()).add(uid);
            }
        }

        // 组装 RetryMessage
        List<RetryMessage> retryMessages = messageEntities.stream()
                .map(msg -> {
                    RetryMessage retryMessage = new RetryMessage();
                    retryMessage.setMessageEntity(msg);
                    retryMessage.setReceiverUids(msgIdToReceivers.getOrDefault(msg.getId(), Collections.emptyList()));
                    return retryMessage;
                })
                .toList();

        // 进行重试消息发送
        retryMessages.forEach(retryMessage -> {
            mqProducer.sendSecureMsg(MQConstant.MSG_RETRY_TOPIC, retryMessage,
                    "retry:" + retryMessage.getMessageEntity().getId());
        });

        // 移除unack和信息简表，只作一次重试
        expiredUnAckMsg.forEach((uid, msgIdSet) -> {
            List<Long> msgIdList = msgIdSet.stream().toList(); // Java 16+ 可用
            msgAckCache.removeUnAckMsg(uid, msgIdList);
        });

        stopWatch.stop();
        log.info("检测到消息未ack，已重新推送，耗时：{}ms", stopWatch.getTotalTimeMillis());
    }
}
