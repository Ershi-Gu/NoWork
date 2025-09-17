package com.ershi.chat.controller;

import com.ershi.chat.service.IUserMsgInboxService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户收件箱表 控制层。
 *
 * @author mybatis-flex-helper automatic generation
 * @since 1.0
 */
@RestController
@RequestMapping("/userMsgInbox")
public class UserMsgInboxController {

    @Resource
    private IUserMsgInboxService userMsgInboxService;
}