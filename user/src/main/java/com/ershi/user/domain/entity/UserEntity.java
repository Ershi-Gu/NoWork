package com.ershi.user.domain.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.lang.Long;
import java.time.LocalDateTime;
import java.util.Date;
import java.lang.String;
import java.lang.Integer;

/**
 * 用户表 实体类。
 *
 * @author mybatis-flex-helper automatic generation
 * @since 1.0
 */
@Data
@Table(value = "user")
public class UserEntity {

    /**
     * 用户id
     */
    @Id(keyType = KeyType.Auto)
    private Long id;

    /**
     * 用户账号
     */
    @Column(value = "account")
    private String account;

    /**
     * 用户密码
     */
    @Column(value = "password")
    private String password;

    /**
     * 用户名
     */
    @Column(value = "name")
    private String name;

    /**
     * 微信open_id
     */
    @Column(value = "wx_open_id")
    private String wxOpenId;

    /**
     * 绑定邮箱
     */
    @Column(value = "email")
    private String email;

    /**
     * 用户头像url
     */
    @Column(value = "avatar_url")
    private String avatarUrl;

    /**
     * 用户简介
     */
    @Column(value = "profile")
    private String profile;

    /**
     * 0-离线，1-在线
     */
    @Column(value = "active_status")
    private Integer activeStatus;

    /**
     * 最后登录时间
     */
    @Column(value = "last_login_time")
    private LocalDateTime lastLoginTime;

    /**
     * ip解析信息
     */
    @Column(value = "ip_info")
    private String ipInfo;

    /**
     * 创建时间
     */
    @Column(value = "create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Column(value = "update_time")
    private LocalDateTime updateTime;

    /**
     * 是否删除：0-否，1-是
     */
    @Column(value = "is_delete")
    private Integer isDelete;
}
