package com.ershi.chat.service.impl;


import cn.hutool.extra.spring.SpringUtil;
import com.ershi.chat.domain.RoomFriendEntity;
import com.ershi.chat.domain.UserApplyEntity;
import com.ershi.chat.domain.enums.UserApplyReadStatusEnum;
import com.ershi.chat.domain.enums.UserApplyStatusEnum;
import com.ershi.chat.domain.enums.UserApplyTypeEnum;
import com.ershi.chat.domain.vo.FriendApplyResp;
import com.ershi.chat.event.ApplyFriendEvent;
import com.ershi.chat.event.FriendAddedEvent;
import com.ershi.chat.mapper.UserApplyMapper;
import com.ershi.chat.service.IRoomFriendService;
import com.ershi.chat.service.IUserApplyService;
import com.ershi.chat.service.IUserFriendService;
import com.ershi.chat.service.adapter.UserApplyAdapter;
import com.ershi.common.aop.RedissonLock;
import com.ershi.common.exception.BusinessErrorEnum;
import com.ershi.common.exception.SystemCommonErrorEnum;
import com.ershi.common.utils.AssertUtil;
import com.ershi.common.utils.RequestHolder;
import com.ershi.user.service.IUserService;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static com.ershi.chat.domain.table.UserApplyEntityTableDef.USER_APPLY_ENTITY;


/**
 * 用户申请表 服务层实现。
 *
 * @author mybatis-flex-helper automatic generation
 * @since 1.0
 */
@Slf4j
@Service
public class UserApplyServiceImpl extends ServiceImpl<UserApplyMapper, UserApplyEntity> implements IUserApplyService {

    @Resource
    private IUserFriendService userFriendService;

    @Resource
    private IUserService userService;

    @Resource
    private IRoomFriendService roomFriendService;

    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void applyFriend(Long targetUid, String targetAccount, String applyMsg) {
        // 判断是id申请还是账号申请，若是账号申请则查询id
        Long realTargetUid = resolveTargetUid(targetUid, targetAccount);

        // 获取当前用户uid
        Long uid = RequestHolder.get().getUid();

        // 判断是否已是好友
        boolean isFriend = userFriendService.isFriend(uid, realTargetUid);
        AssertUtil.isFalse(isFriend, BusinessErrorEnum.FRIEND_EXIST_ERROR);

        // 查询是否已存在待审批的请求，已存在记录则直接返回
        if (hasPendingApply(uid, realTargetUid)) {
            log.info("已存在申请记录，uid：{}，targetId：{}", uid, realTargetUid);
            return;
        }

        // 查询对方是否申请添加自己，若是则直接同意
        UserApplyEntity incomingApply = findIncomingApply(uid, realTargetUid);
        if (incomingApply != null) {
            // 同类调用避免事务失效
            SpringUtil.getBean(UserApplyServiceImpl.class)
                    .approveFriend(incomingApply.getId(), UserApplyStatusEnum.ACCEPTED.getType());
            return;
        }

        // 记录db
        UserApplyEntity userApplyEntity = UserApplyAdapter.buildApplyFriendRecord(uid, targetUid, applyMsg);
        boolean save = this.save(userApplyEntity);
        AssertUtil.isTrue(save, SystemCommonErrorEnum.DB_ERROR);

        // 发布好友申请事件 -> 发送消息通知目标
        applicationEventPublisher.publishEvent(new ApplyFriendEvent(this, userApplyEntity));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @RedissonLock(key = "#applyId")
    public void approveFriend(Long applyId, Integer status) {
        Long uid = RequestHolder.get().getUid();
        AssertUtil.isTrue(Stream.of(applyId, status).allMatch(Objects::nonNull), BusinessErrorEnum.API_PARAM_ERROR);

        // 查询申请记录
        UserApplyEntity userApplyEntity = this.getOne(QueryWrapper.create().where(USER_APPLY_ENTITY.ID.eq(applyId)
                .and(USER_APPLY_ENTITY.STATUS.eq(UserApplyStatusEnum.PENDING))));

        // 合法性校验
        AssertUtil.nonNull(userApplyEntity, BusinessErrorEnum.APPLY_NOT_EXIST_ERROR);
        AssertUtil.isTrue(Objects.equals(userApplyEntity.getTargetId(), uid),
                BusinessErrorEnum.APPLY_NOT_EXIST_ERROR);

        // 修改保存申请记录状态，db
        userApplyEntity.setStatus(status);
        this.updateById(userApplyEntity);

        // 创建双方好友关系，db，注意此处targetId相反
        userFriendService.createFriend(uid, userApplyEntity.getUid());

        // 创建单聊房间，db
        RoomFriendEntity roomFriendEntity = roomFriendService.createRoomFriend(uid, userApplyEntity.getUid());

        // 发送好友添加成功事件 -> 向单聊房间发送一条消息
        applicationEventPublisher.publishEvent(new FriendAddedEvent(this, uid, roomFriendEntity));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<FriendApplyResp> getFriendApplyList() {
        Long uid = RequestHolder.get().getUid();
        // 未登录状态下直接返回空列表
        if (uid == null) {
            return List.of();
        }

        // 查询前10条记录，待审批的在前
        List<UserApplyEntity> userApplyList =
                this.list(QueryWrapper.create().where(USER_APPLY_ENTITY.TARGET_ID.eq(uid))
                        .limit(10)
                        .orderBy(UserApplyEntity::getStatus, true));

        // 已读当前用户所有好友申请记录
        SpringUtil.getBean(UserApplyServiceImpl.class).readApples(uid, UserApplyTypeEnum.FRIEND.getType());

        return userApplyList.stream().map(UserApplyAdapter::buildFriendApplyListResp).toList();
    }

    /**
     * 获取最终请求目标uid，可根据账号查询，也可直接传入
     *
     * @param targetUid
     * @param targetAccount
     * @return {@link Long }
     */
    private Long resolveTargetUid(Long targetUid, String targetAccount) {
        return Optional.ofNullable(targetUid)
                .orElseGet(() -> {
                    AssertUtil.isNotBlank(targetAccount, BusinessErrorEnum.API_PARAM_ERROR, "申请好友时请输入对方账号");
                    Long uid = userService.getUidByAccount(targetAccount);
                    AssertUtil.nonNull(uid, BusinessErrorEnum.USER_NOT_EXIST_ERROR);
                    return uid;
                });
    }

    /**
     * 查询是否已存在申请记录
     *
     * @param uid
     * @param realTargetUid
     * @return boolean
     */
    private boolean hasPendingApply(Long uid, Long realTargetUid) {
        return this.exists(QueryWrapper.create().where(USER_APPLY_ENTITY.UID.eq(uid)
                .and(USER_APPLY_ENTITY.TARGET_ID.eq(realTargetUid)
                        .and(USER_APPLY_ENTITY.STATUS.eq(UserApplyStatusEnum.PENDING)))));
    }

    /**
     * 查找对方是否已申请添加过自己
     *
     * @param uid
     * @param realTargetUid
     * @return {@link UserApplyEntity }
     */
    private UserApplyEntity findIncomingApply(Long uid, Long realTargetUid) {
        return this.getOne(QueryWrapper.create().where(USER_APPLY_ENTITY.TARGET_ID.eq(uid)
                .and(USER_APPLY_ENTITY.UID.eq(realTargetUid))
                .and(USER_APPLY_ENTITY.STATUS.eq(UserApplyStatusEnum.PENDING.getType()))));
    }

    /**
     * 已读申请，按照申请type分类
     *
     * @param uid
     * @param type
     */
    private void readApples(Long uid, Integer type) {
        // 修改当前用户所有type类型的申请为已读
        UserApplyEntity update = UserApplyEntity.builder()
                .readStatus(UserApplyReadStatusEnum.READ.getType())
                .build();
        this.update(update, QueryWrapper.create()
                .where(USER_APPLY_ENTITY.TARGET_ID.eq(uid)
                        .and(USER_APPLY_ENTITY.TYPE.eq(type))
                        .and(USER_APPLY_ENTITY.STATUS.eq(UserApplyStatusEnum.PENDING.getType()))));
    }
}