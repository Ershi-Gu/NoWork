package com.ershi.hotboard.datasource;

import com.ershi.hotboard.domain.entity.HotBoardEntity;
import com.ershi.hotboard.domain.enums.HBDataTypeEnum;

/**
 * 热点数据获取数据源，若添加数据源需实现该接口
 *
 * @author Ershi-Gu.
 * @since 2025-09-03
 */
public interface DataSource {

    /**
     * 指定当前数据源类型枚举
     *
     * @return {@link HBDataTypeEnum }
     */
    HBDataTypeEnum getTypeEnum();

    /**
     * 获取热榜数据
     *
     * @return {@link HotBoardEntity }
     */
    HotBoardEntity getHotBoardData();
}
