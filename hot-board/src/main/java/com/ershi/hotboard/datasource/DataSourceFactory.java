package com.ershi.hotboard.datasource;

import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据源加载工厂，用于集中加载所有数据源
 *
 * @author Ershi-Gu.
 * @since 2025-09-03
 */
@Component
public class DataSourceFactory {

    /** 存放数据源实例 */
    private final Map<String, DataSource> dataSourceMap = new HashMap<>();

    /**
     * 加载所有实现了DataSource接口的数据源到map
     *
     * @param dataSources
     */
    public DataSourceFactory(List<DataSource> dataSources) {
        dataSources.forEach(ds -> dataSourceMap.put(ds.getTypeEnum().getType(), ds));
    }

    /**
     * 获取所有数据源
     *
     * @return {@link Collection }<{@link DataSource }>
     */
    public Collection<DataSource> getAllDataSources() {
        return dataSourceMap.values();
    }
}
