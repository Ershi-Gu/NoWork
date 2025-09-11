package com.ershi.chat.service;


import com.ershi.chat.domain.RoomEntity;
import com.mybatisflex.core.service.IService;

/**
 * 房间表 服务层。
 *
 * @author mybatis-flex-helper automatic generation
 * @since 1.0
 */
public interface IRoomService extends IService<RoomEntity> {

    /**
     * 创建房间
     *
     * @param type
     * @param hotFlag
     */
    RoomEntity createRoom(Integer type, Integer hotFlag);
}