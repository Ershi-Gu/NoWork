package com.ershi.user.service.impl;


import org.springframework.stereotype.Service;
import com.ershi.user.service.IUserService;
import com.ershi.user.domain.entity.UserEntity;
import com.ershi.user.mapper.UserMapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;

/**
 * 用户表 服务层实现。
 *
 * @author mybatis-flex-helper automatic generation
 * @since 1.0
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements IUserService {

}