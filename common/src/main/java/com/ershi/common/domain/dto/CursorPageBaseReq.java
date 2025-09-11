package com.ershi.common.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mybatisflex.core.paginate.Page;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;


/**
 * 游标翻页请求
 *
 * @author Ershi
 * @since 2025-09-10
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CursorPageBaseReq {

    /**
     * 页面大小，默认10
     */
    @Min(0)
    @Max(100)
    private Integer pageSize = 10;

    /**
     * 游标（初始为null，后续请求附带上次翻页的游标）
     */
    private String cursor;

    /**
     * 初始化page
     *
     * @return {@link Page }
     */
    public Page initPage() {
        return new Page(1, this.pageSize);
    }

    /**
     * 判断游标是否是第一页
     * @return {@link Boolean}
     */
    @JsonIgnore
    public Boolean isFirstPage() {
        return StringUtils.isEmpty(cursor);
    }
}

