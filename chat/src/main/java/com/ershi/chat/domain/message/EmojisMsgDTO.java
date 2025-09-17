package com.ershi.chat.domain.message;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * emoji表情参数
 * @author Ershi
 * @date 2025/01/13
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmojisMsgDTO implements BaseMsgDTO, Serializable {

    private static final long serialVersionUID = 8306969784765531308L;

    /**
     * 下载地址
     */
    @NotBlank
    private String url;
}


