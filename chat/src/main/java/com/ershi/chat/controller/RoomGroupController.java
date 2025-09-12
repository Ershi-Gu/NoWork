package com.ershi.chat.controller;

import com.ershi.chat.domain.dto.GroupCreateReq;
import com.ershi.chat.domain.vo.GroupCreateResp;
import com.ershi.chat.service.IRoomGroupService;
import com.ershi.common.domain.vo.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 群聊房间表 控制层。
 *
 * @author mybatis-flex-helper automatic generation
 * @since 1.0
 */
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
}