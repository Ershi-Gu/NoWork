package com.ershi.chat.domain.message.type;

import com.ershi.chat.domain.message.BaseFileDTO;
import com.ershi.chat.domain.message.BaseMsgDTO;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * 语音消息参数
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class FileMsgDTO extends BaseFileDTO implements BaseMsgDTO, Serializable {

    private static final long serialVersionUID = 7625826642147680870L;

    /**
     * 文件名
     */
    @NotNull
    private String fileName;

}
