package com.ershi.chat.domain.message;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * 文件基类
 * @author Ershi
 * @date 2025/01/13
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class BaseFileDTO implements Serializable {

    private static final long serialVersionUID = -3003466224804088047L;

    /**
     * 大小（字节）
     */
    @NotNull
    private Long size;

    /**
     * 文件地址url
     */
    @NotBlank
    private String url;
}
