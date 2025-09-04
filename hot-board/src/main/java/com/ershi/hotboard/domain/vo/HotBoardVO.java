package com.ershi.hotboard.domain.vo;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSON;
import com.ershi.hotboard.domain.dto.HotBoardDataDTO;
import com.ershi.hotboard.domain.entity.HotBoardEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 热榜展示VO
 *
 * @author Ershi-Gu.
 * @since 2025-09-04
 */
@Data
public class HotBoardVO implements Serializable {

    private static final long serialVersionUID = -6179607588125078857L;

    /**
     * 热榜id
     */
    private Long id;

    /**
     * 热榜名称
     */
    private String name;

    /**
     * 数据源类型
     */
    private String type;

    /**
     * 数据源类型中文名
     */
    private String typeName;

    /**
     * 热榜图标url
     */
    private String iconUrl;

    /**
     * 热榜数据
     */
    private List<HotBoardDataDTO> data;

    /**
     * 热榜分类
     */
    private Integer category;

    /**
     * 实体类转换为VO
     *
     * @param hotBoardEntity
     * @return {@link HotBoardVO } 若入参为空则也返回空
     */
    public static HotBoardVO objToVO(HotBoardEntity hotBoardEntity) {
        if (hotBoardEntity == null) {
            return null;
        }

        HotBoardVO hotBoardVO = new HotBoardVO();
        BeanUtil.copyProperties(hotBoardEntity, hotBoardVO);
        hotBoardVO.setData(JSON.parseArray(hotBoardEntity.getDataJson(), HotBoardDataDTO.class));
        return hotBoardVO;
    }


}
