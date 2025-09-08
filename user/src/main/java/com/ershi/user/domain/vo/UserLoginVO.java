package com.ershi.user.domain.vo;

import lombok.Data;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 用户登录返回类
 *
 * @author Ershi-Gu.
 * @since 2025-09-08
 */
@Data
public class UserLoginVO {

    /**
     * 用户id
     */
    private Long id;

    /**
     * 登录凭证
     */
    private String token;

    /**
     * 用户账号
     */
    private String account;

    /**
     * 用户名
     */
    private String name;

    /**
     * 微信open_id
     */
    private String wxOpenId;

    /**
     * 绑定邮箱
     */
    private String email;

    /**
     * 用户头像url
     */
    private String avatarUrl;

    /**
     * 用户简介
     */
    private String profile;

    /**
     * 0-离线，1-在线
     */
    private Integer activeStatus;

    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;

    /**
     * todo 用户登录返回 ip解析信息
     */
//    private String ipInfo;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}
