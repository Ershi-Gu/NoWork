package com.ershi.user.controller;

import com.ershi.common.resp.ApiResult;
import com.ershi.user.domain.dto.UserEmailCaptchaReq;
import com.ershi.user.domain.dto.UserEmailRegisterReq;
import com.ershi.user.domain.dto.UserLoginReq;
import com.ershi.user.domain.vo.UserLoginVO;
import com.ershi.user.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户表 控制层。
 *
 * @author mybatis-flex-helper automatic generation
 * @since 1.0
 */
@Tag(name = "用户模块")
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private IUserService userService;

    @Operation(summary = "发送邮箱注册验证码")
    @PostMapping("/email/captcha")
    public ApiResult<Void> sendEmailCaptcha(@RequestBody UserEmailCaptchaReq userEmailCaptchaReq) {
        userService.sendEmailCaptcha(userEmailCaptchaReq.getEmail());
        return ApiResult.success();
    }

    @Operation(summary = "邮箱注册")
    @PostMapping("/email/register")
    public ApiResult<String> emailRegister(@RequestBody UserEmailRegisterReq userEmailRegisterReq) {
        return ApiResult.success(userService.emailRegister(userEmailRegisterReq));
    }

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public ApiResult<UserLoginVO> userLogin(@RequestBody UserLoginReq userLoginReq) {
        return ApiResult.success(userService.login(userLoginReq));
    }

    @Operation(summary = "用户登出")
    @PostMapping("/logout")
    public ApiResult<Void> userLogout() {
        return ApiResult.success();
    }
}