package com.ershi.chat.domain.vo;

import lombok.Builder;
import lombok.Data;


/**
 * 好友申请信息返回
 * @author Ershi
 * @date 2024/12/30
 */
@Data
public class FriendApplyResp {

    /**
     * 申请id
     */
    private Long id;

    /**
     * 申请人uid
     */
    private Long uid;

    /**
     * 申请类型 1加好友
     */
    private Integer type;

    /**
     * 申请信息
     */
    private String msg;

    /**
     * 申请状态 0待审批 1同意 2拒绝
     */
    private Integer status;
}
