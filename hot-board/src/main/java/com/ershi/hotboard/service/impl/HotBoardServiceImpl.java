package com.ershi.hotboard.service.impl;

import com.alibaba.fastjson2.JSON;
import com.ershi.common.constants.RedisKey;
import com.ershi.common.utils.JsonUtils;
import com.ershi.common.utils.RedisUtils;
import com.ershi.hotboard.domain.dto.HotBoardDataDTO;
import com.ershi.hotboard.domain.vo.HotBoardVO;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.stereotype.Service;
import com.ershi.hotboard.service.IHotBoardService;
import com.ershi.hotboard.domain.entity.HotBoardEntity;
import com.ershi.hotboard.mapper.HotBoardMapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.ershi.hotboard.domain.entity.table.HotBoardEntityTableDef.HOT_BOARD_ENTITY;

/**
 * 热榜信息表 服务层实现。
 *
 * @author mybatis-flex-helper automatic generation
 * @since 1.0
 */
@Service
public class HotBoardServiceImpl extends ServiceImpl<HotBoardMapper, HotBoardEntity> implements IHotBoardService {

    /**
     * 热榜缓存数据过期时间
     */
    public static final int HOT_BOARD_LIST_EXPIRE_TIME = 30 * 60;

    @Override
    public List<HotBoardVO> getHotBoardList() {
        String key = RedisKey.getKey(RedisKey.HOT_BOARD_LIST_KEY);

        // 尝试获取缓存
        Set<String> cacheSet = RedisUtils.sGet(key);
        if (cacheSet != null && !cacheSet.isEmpty()) {
            return cacheSet.stream()
                    .map(json -> JSON.parseObject(json, HotBoardVO.class))
                    .collect(Collectors.toList());
        }

        // 无缓存则进行旁路缓存更新
        List<HotBoardVO> voList = this.list(QueryWrapper.create().orderBy(HOT_BOARD_ENTITY.SORT, true))
                .stream().map(HotBoardVO::objToVO).toList();
        RedisUtils.sSet(key, voList.toArray());

        return voList;
    }
}