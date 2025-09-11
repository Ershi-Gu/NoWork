package com.ershi.chat.service.impl;


import org.springframework.stereotype.Service;
import com.ershi.chat.service.IRoomService;
import com.ershi.chat.domain.RoomEntity;
import com.ershi.chat.mapper.RoomMapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;

/**
 * 房间表 服务层实现。
 *
 * @author mybatis-flex-helper automatic generation
 * @since 1.0
 */
@Service
public class RoomServiceImpl extends ServiceImpl<RoomMapper, RoomEntity> implements IRoomService {

    @Override
    public RoomEntity createRoom(Integer type, Integer hotFlag) {
        RoomEntity roomEntity = new RoomEntity();
        roomEntity.setType(type);
        roomEntity.setHotFlag(hotFlag);
        this.save(roomEntity);
        return roomEntity;
    }
}