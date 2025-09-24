package com.ershi.chat.event.listener;

import com.ershi.chat.domain.UserApplyEntity;
import com.ershi.chat.event.ApplyFriendEvent;
import com.ershi.chat.mapper.UserApplyMapper;
import com.ershi.chat.websocket.domain.enums.WSRespTypeEnum;
import com.ershi.chat.websocket.domain.vo.WSApplyFriendResp;
import com.ershi.chat.websocket.domain.vo.WSBaseResp;
import com.ershi.chat.websocket.service.ChatWebSocketService;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Collections;

import static com.ershi.chat.domain.table.UserApplyEntityTableDef.USER_APPLY_ENTITY;

/**
 * 好友申请事件监听器
 *
 * @author Ershi-Gu.
 * @since 2025-09-24
 */
@Component
public class ApplyFriendEventListener {

    @Resource
    private UserApplyMapper userApplyMapper;

    @Resource
    private ChatWebSocketService chatWebSocketService;

    /**
     * 好友申请提交后通知目标对象
     *
     * @param event
     */
    @Async
    @EventListener(classes = ApplyFriendEvent.class)
    public void notifyTargetFriend(ApplyFriendEvent event) {
        UserApplyEntity userApplyEntity = event.getUserApplyEntity();

        // 获取用户申请未读数
        long unReadCount = userApplyMapper.selectCountByQuery(QueryWrapper.create()
                .where(USER_APPLY_ENTITY.TARGET_ID.eq(userApplyEntity.getTargetId())));

        // 推送通知
        chatWebSocketService.sendMsgToUser(Collections.singletonList(userApplyEntity.getTargetId()),
                WSBaseResp.build(WSRespTypeEnum.APPLY_FRIEND.getType(),
                        new WSApplyFriendResp(userApplyEntity.getUid(), unReadCount)));
    }
}