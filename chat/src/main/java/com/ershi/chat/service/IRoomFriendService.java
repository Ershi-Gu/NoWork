package com.ershi.chat.service;


import com.ershi.chat.domain.RoomFriendEntity;
import com.mybatisflex.core.service.IService;

/**
 * 单聊房间表 服务层。
 *
 * @author mybatis-flex-helper automatic generation
 * @since 1.0
 */
public interface IRoomFriendService extends IService<RoomFriendEntity> {

    /**
     * 创建单聊好友房间
     *
     * @param uid
     * @param fid
     */
    RoomFriendEntity createRoomFriend(Long uid, Long fid);

    /**
     * 恢复被禁用的房间
     *
     * @param roomFriendEntity
     */
    void recoverRoomFriend(RoomFriendEntity roomFriendEntity);
}