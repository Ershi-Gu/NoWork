package com.ershi.hotboard.datasource;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ershi.hotboard.domain.dto.HotBoardDataDTO;
import com.ershi.hotboard.domain.entity.HotBoardEntity;
import com.ershi.hotboard.domain.enums.HBCategoryEnum;
import com.ershi.hotboard.domain.enums.HBDataTypeEnum;
import com.ershi.hotboard.domain.enums.HBUpdateIntervalEnum;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * 掘金热榜数据源 <br>
 * https://juejin.cn/hot/articles
 *
 * @author Ershi-Gu.
 * @since 2025-09-04
 */
@Component
public class JueJinDataSource implements DataSource {

    /**
     * 稀土掘金热榜获取api
     */
    private static final String HOT_URL = "https://api.juejin.cn/content_api/v1/content/article_rank";
    /**
     * 帖子url前缀，拼接content_id
     */
    private static final String POST_URL = "https://juejin.cn/post/";


    @Override
    public HBDataTypeEnum getTypeEnum() {
        return HBDataTypeEnum.JUE_JIN;
    }

    @Override
    public HotBoardEntity getHotBoardData() {
        // 补充请求参数
        Map<String, Object> paramsMap = Map.of(
                "category_id", "1",
                "type", "hot",
                "aid", "2608",
                "uuid", "7524995900500135212",
                "spider", "0"
        );

        // 请求api获取热榜数据
        String res = HttpRequest.get(HOT_URL).form(paramsMap).execute().body();

        // 解析数据，构建dataDTO
        JSONArray origDataList = JSON.parseObject(res).getJSONArray("data");

        List<HotBoardDataDTO> dataList = origDataList.stream().map(item -> {
            JSONObject itemJson = (JSONObject) item;

            JSONObject content = itemJson.getJSONObject("content");
            String title = content.getString("title");
            String contentId = content.getString("content_id");
            Integer heat = itemJson.getJSONObject("content_counter").getInteger("view");

            return HotBoardDataDTO.builder()
                    .title(title)
                    .heat(heat)
                    .url(POST_URL + contentId)
                    .build();
        }).toList();

        return HotBoardEntity.builder()
                .name("掘金综合热榜")
                .type(HBDataTypeEnum.JUE_JIN.getType())
                .typeName(HBDataTypeEnum.JUE_JIN.getDesc())
                .iconUrl("https://lf3-cdn-tos.bytescm.com/obj/static/xitu_juejin_web//static/favicon.ico")
                // 按照热度降序取前20个
                .dataJson(JSON.toJSONString(
                        dataList.stream()
                                .sorted(Comparator.comparingInt(HotBoardDataDTO::getHeat).reversed())
                                .toList()
                ))
                .category(HBCategoryEnum.CODE.getType())
                .updateInterval(HBUpdateIntervalEnum.HALF_HOUR.getValue())
                // 分类类别作为排序值，0 最大
                .sort(HBCategoryEnum.CODE.getType())
                .build();
    }

}
