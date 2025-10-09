package com.ershi.chat.domain.vo;

import com.ershi.chat.domain.enums.GroupMemberRoleEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 群聊信息返回
 *
 * @author Ershi
 * @since 2025-10-09
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupInfoResp {

    /**
     * 房间ID
     */
    private Long roomId;

    /**
     * 群聊名称
     */
    private String groupName;

    /**
     * 群聊头像
     */
    private String avatar;

    /**
     * 用户在群内的角色
     *
     * @see GroupMemberRoleEnum
     */
    private Integer role;
}
