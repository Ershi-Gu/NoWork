package com.ershi.chat.service.impl;


import com.ershi.chat.domain.dto.FriendApplyReq;
import com.ershi.common.utils.RequestHolder;
import org.springframework.stereotype.Service;
import com.ershi.chat.service.IUserApplyService;
import com.ershi.chat.domain.UserApplyEntity;
import com.ershi.chat.mapper.UserApplyMapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;

/**
 * 用户申请表 服务层实现。
 *
 * @author mybatis-flex-helper automatic generation
 * @since 1.0
 */
@Service
public class UserApplyServiceImpl extends ServiceImpl<UserApplyMapper, UserApplyEntity> implements IUserApplyService {

    @Override
    public void applyFriend(FriendApplyReq friendApplyReq) {
        // 获取当前用户uid
        Long uid = RequestHolder.get().getUid();

        // 判断是否已是好友

        // 查询是否已存在待审批的请求

        // 记录db


    }
}