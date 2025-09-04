package com.ershi.hotboard.domain.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * 热榜数据展示VO
 *
 * @author Ershi-Gu.
 * @since 2025-09-03
 */
@Builder
@Getter
public class HotBoardDataDTO {

    /** 标题 */
    private String title;

    /** 热度 */
    private Integer heat;

    /** 跳转url */
    private String url;
}
