package com.ershi.chat.service.impl;


import org.springframework.stereotype.Service;
import com.ershi.chat.service.IMessageService;
import com.ershi.chat.domain.message.MessageEntity;
import com.ershi.chat.mapper.MessageMapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;

/**
 * 消息表 服务层实现。
 *
 * @author mybatis-flex-helper automatic generation
 * @since 1.0
 */
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, MessageEntity> implements IMessageService {

}