package com.ershi.hotboard.service;

import com.ershi.hotboard.domain.entity.HotBoardEntity;
import com.ershi.hotboard.domain.dto.HotBoardDataDTO;
import com.ershi.hotboard.domain.vo.HotBoardVO;
import com.mybatisflex.core.service.IService;

import java.util.List;

/**
 * 热榜信息表 服务层。
 *
 * @author mybatis-flex-helper automatic generation
 * @since 1.0
 */
public interface IHotBoardService extends IService<HotBoardEntity> {

    /**
     * 获取所有热榜数据
     *
     * @return {@link List }<{@link HotBoardDataDTO }>
     */
    List<HotBoardVO> getHotBoardList();
}