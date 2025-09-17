package com.ershi.chat.domain.message.type;

import com.ershi.chat.domain.message.BaseFileDTO;
import com.ershi.chat.domain.message.BaseMsgDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;


/**
 * 视频消息参数
 * @author Ershi
 * @date 2025/01/13
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class VideoMsgDTO extends BaseFileDTO implements BaseMsgDTO, Serializable {
    private static final long serialVersionUID = 2208392440227774319L;

    /**
     * 缩略图宽度（像素）
     */
    @NotNull
    private Integer thumbWidth;

    /**
     * 缩略图高度（像素）
     */
    @NotNull
    private Integer thumbHeight;

    /**
     * 缩略图大小（字节）
     */
    @NotNull
    private Long thumbSize;

    /**
     * 缩略图下载地址
     */
    @NotBlank
    private String thumbUrl;

}
