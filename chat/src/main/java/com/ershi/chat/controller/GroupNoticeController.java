package com.ershi.chat.controller;

import com.ershi.chat.domain.vo.GroupInviteResp;
import com.ershi.chat.service.IGroupInviteService;
import com.ershi.common.domain.vo.ApiResult;
import com.ershi.common.utils.RequestHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 群通知 控制层
 *
 * @author Ershi_Gu
 * @since 2025/10/10
 */
@Tag(name = "群通知模块")
@RestController
@RequestMapping("/group/invite")
public class GroupNoticeController {

    @Resource
    private IGroupInviteService groupInviteService;

    @Operation(summary = "获取当前用户群聊邀请列表-仅展示10条")
    @GetMapping("/list")
    public ApiResult<List<GroupInviteResp>> getGroupInviteList() {
        Long uid = RequestHolder.get().getUid();
        return ApiResult.success(groupInviteService.getGroupInviteList(uid));
    }
}