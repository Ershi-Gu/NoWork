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
 * 图片消息参数
 * @author Ershi
 * @date 2025/01/13
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ImgMsgDTO extends BaseFileDTO implements BaseMsgDTO, Serializable {
    private static final long serialVersionUID = 117224716038325871L;

    /**
     * 宽度（像素)
     */
    @NotNull
    private Integer width;

    /**
     * 高度（像素）
     */
    @NotNull
    private Integer height;

}


