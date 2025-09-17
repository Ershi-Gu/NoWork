package com.ershi.chat.domain.message;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * 语音消息参数
 * @author Ershi
 * @date 2025/01/13
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class SoundMsgDTO extends BaseFileDTO implements BaseMsgDTO, Serializable {
    private static final long serialVersionUID = -1401071204376422615L;

    /**
     * 时长（秒）
     */
    @Size(max = 60, message = "语音时长不能超过60秒哦")
    @NotNull
    private Integer second;
}
