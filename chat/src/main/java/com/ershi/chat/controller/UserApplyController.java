package com.ershi.chat.controller;

import com.ershi.chat.domain.dto.FriendApplyReq;
import com.ershi.chat.service.IUserApplyService;
import com.ershi.common.resp.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户申请表 控制层。
 *
 * @author mybatis-flex-helper automatic generation
 * @since 1.0
 */
@Tag(name = "用户申请相关")
@RestController
@RequestMapping("/apply")
public class UserApplyController {

    @Resource
    private IUserApplyService userApplyService;

    @Operation(summary = "申请添加好友")
    @PostMapping("/friend")
    public ApiResult<Void> apply(@RequestBody FriendApplyReq friendApplyReq) {
        userApplyService.applyFriend(friendApplyReq);
        return ApiResult.success();
    }
}