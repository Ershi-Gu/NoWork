package com.ershi.user.controller;

import com.ershi.common.resp.ApiResult;
import com.ershi.user.domain.dto.UserEmailCaptchaReq;
import com.ershi.user.domain.dto.UserEmailRegisterReq;
import com.ershi.user.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户表 控制层。
 *
 * @author mybatis-flex-helper automatic generation
 * @since 1.0
 */
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
    @PostMapping("/register")
    public ApiResult<Long> register(@RequestBody UserEmailRegisterReq userEmailRegisterReq) {
        return ApiResult.success();
    }
}