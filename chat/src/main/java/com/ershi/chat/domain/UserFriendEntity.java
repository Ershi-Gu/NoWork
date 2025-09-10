package com.ershi.chat.domain;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Builder;
import lombok.Data;

import java.lang.Long;
import java.util.Date;
import java.lang.Integer;

/**
 * 好友表 实体类。
 *
 * @author mybatis-flex-helper automatic generation
 * @since 1.0
 */
@Data
@Builder
@Table(value = "user_friend")
public class UserFriendEntity {

    /**
     * 好友id
     */
    @Id(keyType = KeyType.Auto)
    private Long id;

    /**
     * 用户id
     */
    @Column(value = "uid")
    private Long uid;

    /**
     * 好友id
     */
    @Column(value = "fid")
    private Long fid;

    /**
     * 创建时间
     */
    @Column(value = "create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @Column(value = "update_time")
    private Date updateTime;

    /**
     * 是否删除：0-否，1-是
     */
    @Column(value = "is_delete")
    private Integer isDelete;
}
