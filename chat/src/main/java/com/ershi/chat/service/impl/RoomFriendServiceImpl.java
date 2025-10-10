package com.ershi.chat.service.impl;


import cn.hutool.extra.spring.SpringUtil;
import com.ershi.chat.domain.RoomEntity;
import com.ershi.chat.domain.RoomFriendEntity;
import com.ershi.chat.domain.enums.RoomFriendStatusEnum;
import com.ershi.chat.domain.enums.RoomHotFlagEnum;
import com.ershi.chat.domain.enums.RoomTypeEnum;
import com.ershi.chat.mapper.RoomFriendMapper;
import com.ershi.chat.service.IRoomFriendService;
import com.ershi.chat.service.IRoomService;
import com.ershi.common.exception.BusinessErrorEnum;
import com.ershi.common.utils.AssertUtil;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.ershi.chat.domain.table.RoomFriendEntityTableDef.ROOM_FRIEND_ENTITY;

/**
 * 单聊房间表 服务层实现。
 *
 * @author mybatis-flex-helper automatic generation
 * @since 1.0
 */
@Slf4j
@Service
public class RoomFriendServiceImpl extends ServiceImpl<RoomFriendMapper, RoomFriendEntity> implements IRoomFriendService {

    @Resource
    private IRoomService roomService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RoomFriendEntity createRoomFriend(Long uid, Long fid) {
        AssertUtil.isFalse(ObjectUtils.anyNull(uid, fid), BusinessErrorEnum.API_PARAM_ERROR);

        // 生成单聊房间唯一key
        String RFKey = generateRFkey(uid, fid);

        // 检查room是否已存在
        RoomFriendEntity roomFriendEntity = this.getOne(QueryWrapper.create().
                where(ROOM_FRIEND_ENTITY.ROOM_KEY.eq(RFKey)));
        // 已存在则恢复房间
        if (roomFriendEntity != null) {
            SpringUtil.getBean(RoomFriendServiceImpl.class).recoverRoomFriend(roomFriendEntity);
        } else {
            // 创建room
            RoomEntity roomEntity = roomService.createRoom(RoomTypeEnum.FRIEND.getType(), RoomHotFlagEnum.NOT_HOT.getType());
            // 创建roomFriend
            roomFriendEntity = RoomFriendEntity.build(roomEntity.getId(), RFKey, uid, fid);
            this.save(roomFriendEntity);
        }

        return roomFriendEntity;
    }

    @Override
    public void recoverRoomFriend(RoomFriendEntity roomFriendEntity) {
        AssertUtil.nonNull(roomFriendEntity, BusinessErrorEnum.API_PARAM_ERROR);

        if (!RoomFriendStatusEnum.BAN.getStatus().equals(roomFriendEntity.getStatus())) {
            log.info("单聊房间：{} 正常启用中，无需恢复", roomFriendEntity.getId());
            return;
        }
        roomFriendEntity.setStatus(RoomFriendStatusEnum.NORMAL.getStatus());
        this.updateById(roomFriendEntity);
    }

    @Override
    public RoomFriendEntity getRoomFriend(Long uid1, Long uid2) {
        String RFKey = generateRFkey(uid1, uid2);
        return this.getOne(QueryWrapper.create().where(ROOM_FRIEND_ENTITY.ROOM_KEY.eq(RFKey)));
    }

    /**
     * 生成单聊房间唯一key，小的uid在前
     *
     * @param uid
     * @param fid
     * @return {@link String } uid(min)_uid(max)
     */
    private String generateRFkey(Long uid, Long fid) {
        return Math.min(uid, fid) + "_" + Math.max(uid, fid);
    }
}