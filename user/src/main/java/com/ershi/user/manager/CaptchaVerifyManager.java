package com.ershi.user.manager;

import com.anji.captcha.model.common.ResponseModel;
import com.anji.captcha.model.vo.CaptchaVO;
import com.anji.captcha.service.CaptchaService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * 滑块验证码二次校验用
 *
 * @author Ershi-Gu.
 * @since 2025-09-08
 */
@Component
public class CaptchaVerifyManager {

    @Resource
    private CaptchaService captchaService;

    /**
     * 二次校验验证码
     *
     * @param captchaVerification
     * @return boolean 校验成功返回true
     */
    public boolean verify(String captchaVerification) {
        CaptchaVO captchaVO = new CaptchaVO();
        captchaVO.setCaptchaVerification(captchaVerification);
        ResponseModel response = captchaService.verification(captchaVO);

        //验证码校验失败，返回信息告诉前端
        //repCode  0000  无异常，代表成功
        //repCode  9999  服务器内部异常
        //repCode  0011  参数不能为空
        //repCode  6110  验证码已失效，请重新获取
        //repCode  6111  验证失败
        //repCode  6112  获取验证码失败,请联系管理员
        return response.isSuccess();
    }
}
