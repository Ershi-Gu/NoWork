package com.ershi.chat.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.ershi.chat.domain.vo.FriendResp;
import com.ershi.chat.service.IUserFriendService;
import com.ershi.common.domain.dto.CursorPageBaseReq;
import com.ershi.common.domain.vo.ApiResult;
import com.ershi.common.domain.vo.CursorPageBaseResp;
import com.ershi.common.utils.RequestHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 好友表 控制层。
 *
 * @author mybatis-flex-helper automatic generation
 * @since 1.0
 */
@Tag(name = "好友模块")
@RestController
@RequestMapping("/friend")
public class UserFriendController {

    @Resource
    private IUserFriendService userFriendService;

    @Operation(summary = "滑动获取好友列表")
    @GetMapping("/page")
    @SaCheckLogin
    public ApiResult<CursorPageBaseResp<FriendResp>> getFriendPage(@Validated CursorPageBaseReq cursorPageBaseReq) {
            Long uid = RequestHolder.get().getUid();
            return ApiResult.success(userFriendService.getFriendPage(uid,
                    cursorPageBaseReq));
    }
}