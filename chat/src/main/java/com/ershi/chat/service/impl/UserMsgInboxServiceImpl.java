package com.ershi.chat.service.impl;


import org.springframework.stereotype.Service;
import com.ershi.chat.service.IUserMsgInboxService;
import com.ershi.chat.domain.UserMsgInboxEntity;
import com.ershi.chat.mapper.UserMsgInboxMapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;

/**
 * 用户收件箱表 服务层实现。
 *
 * @author mybatis-flex-helper automatic generation
 * @since 1.0
 */
@Service
public class UserMsgInboxServiceImpl extends ServiceImpl<UserMsgInboxMapper, UserMsgInboxEntity> implements IUserMsgInboxService {

}