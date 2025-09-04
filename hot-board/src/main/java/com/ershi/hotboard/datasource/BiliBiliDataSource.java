package com.ershi.hotboard.datasource;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ershi.hotboard.domain.entity.HotBoardEntity;
import com.ershi.hotboard.domain.enums.HBCategoryEnum;
import com.ershi.hotboard.domain.enums.HBDataTypeEnum;
import com.ershi.hotboard.domain.enums.HBUpdateIntervalEnum;
import com.ershi.hotboard.domain.dto.HotBoardDataDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

/**
 * BiliBili 热搜数据源
 *
 * @author Ershi-Gu.
 * @since 2025-09-03
 */
@Slf4j
@Component
public class BiliBiliDataSource implements DataSource {

    /**
     * 热搜获取官方url
     */
    private static final String HOT_URL = "https://api.bilibili.com/x/web-interface/popular";

    @Override
    public HBDataTypeEnum getTypeEnum() {
        return HBDataTypeEnum.BILI_BILI;
    }

    @Override
    public HotBoardEntity getHotBoardData() {
        // 请求第三方API获取数据
        String res = HttpRequest.get(HOT_URL).execute().body();

        // 获取原始数据List
        JSONObject resJson = JSON.parseObject(res);
        JSONArray origDataList = resJson.getJSONObject("data").getJSONArray("list");

        // 构造热榜实际存储数据
        List<HotBoardDataDTO> dataList = origDataList.stream().map(item -> {
            JSONObject itemJson = (JSONObject) item;

            // 标题
            String title = itemJson.getString("title");
            // 跳转url
            String url = itemJson.getString("short_link_v2");
            // 热度
            Integer heat = itemJson.getJSONObject("stat").getInteger("view");

            return HotBoardDataDTO.builder().title(title).heat(heat).url(url).build();
        }).toList();

        return HotBoardEntity.builder()
                .name("B站热搜")
                .type(HBDataTypeEnum.BILI_BILI.getType())
                .typeName(HBDataTypeEnum.BILI_BILI.getDesc())
                .iconUrl("https://www.bilibili.com/favicon.ico")
                // 按照热度降序取前20个
                .dataJson(
                        JSON.toJSONString(
                                dataList.stream()
                                        .sorted(Comparator.comparingInt(HotBoardDataDTO::getHeat).reversed())
                                        .toList()
                        )
                )
                .category(HBCategoryEnum.ENTERTAINMENT.getType())
                .updateInterval(HBUpdateIntervalEnum.HALF_HOUR.getValue())
                // 分类类别作为排序值，0 最大
                .sort(HBCategoryEnum.ENTERTAINMENT.getType())
                .build();
    }
}
