package com.ershi.transaction.domain.entity;

import com.ershi.transaction.domain.dto.SecureInvokeDTO;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.handler.Fastjson2TypeHandler;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.Long;
import java.time.LocalDateTime;
import java.lang.String;
import java.lang.Integer;

/**
 * 本地消息表 实体类。
 *
 * @author mybatis-flex-helper automatic generation
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "secure_invoke_record")
public class SecureInvokeRecordEntity {

    public final static int STATUS_WAIT = 1;

    public final static int STATUS_FAIL = 2;

    /**
     * id
     */
    @Id(keyType = KeyType.Auto)
    private Long id;

    /**
     * 请求快照参数json
     */
    @Column(value = "secure_invoke_json", typeHandler = Fastjson2TypeHandler.class)
    private SecureInvokeDTO secureInvokeJson;

    /**
     * 状态 1待执行 2已失败
     */
    @Column(value = "status")
    private Integer status;

    /**
     * 下一次重试的时间
     */
    @Column(value = "next_retry_time")
    private LocalDateTime nextRetryTime;

    /**
     * 已经重试的次数
     */
    @Column(value = "retry_times")
    private Integer retryTimes;

    /**
     * 最大重试次数
     */
    @Column(value = "max_retry_times")
    private Integer maxRetryTimes;

    /**
     * 执行失败的堆栈
     */
    @Column(value = "fail_reason")
    private String failReason;

    /**
     * 创建时间
     */
    @Column(value = "create_time")
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @Column(value = "update_time")
    private LocalDateTime updateTime;

    /**
     * 是否删除：0-否，1-是
     */
    @Column(value = "is_delete")
    private Integer isDelete;
}
