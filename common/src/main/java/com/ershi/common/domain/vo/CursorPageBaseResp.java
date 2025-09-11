package com.ershi.common.domain.vo;

import cn.hutool.core.collection.CollectionUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 游标翻页返回
 *
 * @author Ershi
 * @since 2025-09-10
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CursorPageBaseResp<T> {

    /**
     * 游标（下次翻页带上这参数）
     */
    private String cursor;

    /**
     * 是否最后一页
     */
    private Boolean isLast = Boolean.FALSE;

    /**
     * 数据列表
     */
    private List<T> dataList;

    /**
     * 初始化游标分页响应对象
     *
     * @param cursorPage 游标分页基础响应对象，用于后续填充数据的
     * @param list       游标分页的数据列表
     * @return {@link CursorPageBaseResp}<{@link T}> 回一个初始化后的游标分页响应对象，包含分页信息和数据列表
     */
    public static <T> CursorPageBaseResp<T> init(CursorPageBaseResp cursorPage, List list) {
        CursorPageBaseResp<T> cursorPageBaseResp = new CursorPageBaseResp<T>();
        cursorPageBaseResp.setIsLast(cursorPage.getIsLast());
        cursorPageBaseResp.setDataList(list);
        cursorPageBaseResp.setCursor(cursorPage.getCursor());
        return cursorPageBaseResp;
    }

    public static <T> CursorPageBaseResp<T> empty() {
        CursorPageBaseResp<T> cursorPageBaseResp = new CursorPageBaseResp<T>();
        cursorPageBaseResp.setIsLast(true);
        cursorPageBaseResp.setDataList(new ArrayList<T>());
        return cursorPageBaseResp;
    }

    @JsonIgnore
    public Boolean isEmpty() {
        return CollectionUtil.isEmpty(dataList);
    }
}
