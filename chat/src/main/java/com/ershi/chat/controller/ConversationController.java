package com.ershi.chat.controller;

import com.ershi.chat.domain.vo.ConversationResp;
import com.ershi.chat.service.IConversationService;
import com.ershi.common.domain.dto.CursorPageBaseReq;
import com.ershi.common.domain.vo.ApiResult;
import com.ershi.common.domain.vo.CursorPageBaseResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 聊天会话api层
 *
 * @author Ershi-Gu.
 * @since 2025-09-25
 */
@Tag(name = "聊天会话")
@RestController
@RequestMapping("/conversation")
public class ConversationController {

    @Resource
    private IConversationService conversationService;

    @GetMapping("/page")
    @Operation(summary = "获取会话列表")
    public ApiResult<CursorPageBaseResp<ConversationResp>> getConversationPage(@Validated CursorPageBaseReq cursorPageBaseReq) {
        return ApiResult.success(conversationService.getConversationPage(cursorPageBaseReq));
    }
}
