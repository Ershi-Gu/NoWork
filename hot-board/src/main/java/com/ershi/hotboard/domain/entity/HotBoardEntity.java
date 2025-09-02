package com.ershi.hotboard.domain.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.lang.Long;
import java.util.Date;
import java.lang.String;
import java.lang.Integer;

/**
 * 热榜信息表 实体类。
 *
 * @author mybatis-flex-helper automatic generation
 * @since 1.0
 */
@Data
@Builder
@Table(value = "hot_board")
public class HotBoardEntity {

    /**
     * 热榜id
     */
    @Id(keyType = KeyType.Auto)
    private Long id;

    /**
     * 热榜名称
     */
    @Column(value = "name")
    private String name;

    /**
     * 数据源类型
     */
    @Column(value = "type")
    private String type;

    /**
     * 数据源类型中文名
     */
    @Column(value = "type_name")
    private String typeName;

    /**
     * 热榜图标url
     */
    @Column(value = "icon_url")
    private String iconUrl;

    /**
     * 数据-json格式
     */
    @Column(value = "data_json")
    private String dataJson;

    /**
     * 热榜分类
     */
    @Column(value = "category")
    private Integer category;

    /**
     * 更新时间间隔，小时单位
     */
    @Column(value = "update_interval")
    private BigDecimal updateInterval;

    /**
     * 展示排序，0最大
     */
    @Column(value = "sort")
    private Integer sort;

    /**
     * 创建时间
     */
    @Column(value = "create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @Column(value = "update_time")
    private Date updateTime;

    /**
     * 是否删除：0-否，1-是
     */
    @Column(value = "is_delete", isLogicDelete = true)
    private Integer isDelete;

}
