package com.ershi.chat.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.ershi.chat.domain.dto.FriendApplyReq;
import com.ershi.chat.domain.dto.FriendApproveReq;
import com.ershi.chat.domain.vo.FriendApplyResp;
import com.ershi.chat.service.IUserApplyService;
import com.ershi.common.domain.vo.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户申请表 控制层。
 *
 * @author mybatis-flex-helper automatic generation
 * @since 1.0
 */
@Tag(name = "好友申请&群聊申请")
@RestController
@RequestMapping("/apply")
public class UserApplyController {

    @Resource
    private IUserApplyService userApplyService;

    @Operation(summary = "申请添加好友")
    @PostMapping("/friend")
    @SaCheckLogin
    public ApiResult<Void> applyFriend(@RequestBody FriendApplyReq friendApplyReq) {
        userApplyService.applyFriend(friendApplyReq.getTargetUid(),
                friendApplyReq.getTargetAccount(), friendApplyReq.getApplyMsg());
        return ApiResult.success();
    }

    @Operation(summary = "审批好友申请")
    @PutMapping("/friend/approve")
    @SaCheckLogin
    public ApiResult<Void> approveFriend(@RequestBody FriendApproveReq friendApproveReq) {
        userApplyService.approveFriend(friendApproveReq.getApplyId(), friendApproveReq.getStatus());
        return ApiResult.success();
    }

    @Operation(summary = "获取好友申请列表-仅展示10条")
    @GetMapping("/friend/list")
    public ApiResult<List<FriendApplyResp>> getFriendApplyList() {
        return ApiResult.success(userApplyService.getFriendApplyList());
    }
}