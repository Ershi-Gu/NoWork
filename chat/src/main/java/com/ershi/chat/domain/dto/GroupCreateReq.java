package com.ershi.chat.domain.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


/**
 * 新建群组请求
 *
 * @author Ershi
 * @date 2025/02/17
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupCreateReq {

    /**
     * 被邀请的用户uid
     */
    @NotNull
    @Size(min = 1, max = 50)
    private List<Long> uidList;
}
