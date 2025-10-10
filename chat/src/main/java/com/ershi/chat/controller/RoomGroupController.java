package com.ershi.chat.controller;

import com.ershi.chat.domain.dto.GroupCreateReq;
import com.ershi.chat.domain.dto.GroupInviteReq;
import com.ershi.chat.domain.vo.GroupCreateResp;
import com.ershi.chat.domain.vo.GroupInfoResp;
import com.ershi.chat.service.IRoomGroupService;
import com.ershi.common.domain.vo.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 群聊房间表 控制层。
 *
 * @author mybatis-flex-helper automatic generation
 * @since 1.0
 */
@Tag(name = "群聊模块")
@RestController
@RequestMapping("/room/group")
public class RoomGroupController {

    @Resource
    private IRoomGroupService roomGroupService;

    @Operation(summary = "新建群聊")
    @PostMapping()
    public ApiResult<GroupCreateResp> createRoomGroup(@Validated @RequestBody GroupCreateReq groupCreateReq) {
        return ApiResult.success(roomGroupService.createRoomGroup(groupCreateReq.getUidList()));
    }

    @Operation(summary = "获取群聊基本信息")
    @GetMapping("/info")
    public ApiResult<GroupInfoResp> getGroupInfo(@RequestParam Long roomId) {
        return ApiResult.success(roomGroupService.getGroupInfo(roomId));
    }

    @Operation(summary = "获取群成员uid列表")
    @GetMapping("/members")
    public ApiResult<List<Long>> getMemberUidList(@RequestParam Long roomId) {
        return ApiResult.success(roomGroupService.getMemberUidList(roomId));
    }

    @Operation(summary = "退出群聊")
    @DeleteMapping("/exit")
    public ApiResult<Void> exitGroup(@RequestParam Long roomId) {
        roomGroupService.exitGroup(roomId);
        return ApiResult.success();
    }

    @Operation(summary = "邀请好友入群")
    @PostMapping("/invite")
    public ApiResult<Void> inviteFriendToGroup(@Validated @RequestBody GroupInviteReq groupInviteReq) {
        roomGroupService.inviteFriendToGroup(groupInviteReq.getRoomId(), groupInviteReq.getFriendUidList());
        return ApiResult.success();
    }

    // todo @Operation(summary = "添加管理员")

    // todo @Operation(summary = "分享群聊")
}