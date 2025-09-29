package com.ershi.common.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.StrUtil;
import com.ershi.common.domain.dto.CursorPageBaseReq;
import com.ershi.common.domain.vo.CursorPageBaseResp;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import com.mybatisflex.core.util.LambdaGetter;
import com.mybatisflex.core.util.LambdaUtil;
import org.springframework.data.redis.core.ZSetOperations;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 游标翻页工具类-可以用于上下滑动场景下的查询，mybatis-flex实现
 *
 * @author Ershi
 * @since 2025-09-10
 */
public class CursorUtils {

    /**
     * Redis-zSet实现的游标翻页查询
     *
     * @param cursorPageBaseReq
     * @param redisKey
     * @param typeConvert
     * @return {@link CursorPageBaseResp }<{@link Pair }<{@link T }, {@link Double }>>
     */
    public static <T> CursorPageBaseResp<Pair<T, Double>> getCursorPageByRedis(CursorPageBaseReq cursorPageBaseReq, String redisKey, Function<String, T> typeConvert) {
        Set<ZSetOperations.TypedTuple<String>> typedTuples;

        // 第一次查询，无游标
        if (StrUtil.isBlank(cursorPageBaseReq.getCursor())) {
            typedTuples = RedisUtils.zReverseRangeWithScores(redisKey, cursorPageBaseReq.getPageSize());
        } else {
            typedTuples = RedisUtils.zReverseRangeByScoreWithScores(redisKey, Double.parseDouble(cursorPageBaseReq.getCursor()), cursorPageBaseReq.getPageSize());
        }

        List<Pair<T, Double>> result = typedTuples
                .stream()
                .map(t -> Pair.of(typeConvert.apply(t.getValue()), t.getScore()))
                .sorted((o1, o2) -> o2.getValue().compareTo(o1.getValue()))
                .collect(Collectors.toList());

        String cursor = Optional.ofNullable(CollectionUtil.getLast(result))
                .map(Pair::getValue)
                .map(String::valueOf)
                .orElse(null);

        Boolean isLast = result.size() != cursorPageBaseReq.getPageSize();

        return new CursorPageBaseResp<>(cursor, isLast, result);
    }

    /**
     * mysql 游标翻页 <br>
     * 1. 该方法用于实现基于MySQL数据库的游标分页查询游标分页是一种高效的分页方式，特别是在处理大量数据时 <br>
     * 2. 它通过记录游标的位置（通常是某一行的特定列值）来实现分页，而不是通过 OFFSET 和 LIMIT 的方式，从而提高了查询效率
     *
     * @param mapper       IService接口的实现类，用于执行数据库操作
     * @param request      包含分页请求信息的对象，如当前游标位置、前进方向等
     * @param initWrapper  用于初始化查询条件的消费者函数，它允许在查询前对查询条件进行自定义设置
     * @param cursorColumn 游标字段列
     * @param order        查询方向，true-升序，false-降序
     * @return 返回一个包含分页查询结果的响应对象，查询不到返回null
     */
    public static <T> CursorPageBaseResp<T> getCursorPageByMysql(IService<T> mapper,
                                                                 CursorPageBaseReq request,
                                                                 Consumer<QueryWrapper> initWrapper,
                                                                 LambdaGetter<T> cursorColumn,
                                                                 boolean order) {
        // 获取列对应实体类属性类型
        Class<?> cursorType = LambdaUtil.getImplClass(cursorColumn);

        // 初始化额外查询条件，用于后续添加游标条件
        QueryWrapper wrapper = QueryWrapper.create();
        initWrapper.accept(wrapper);

        // 设置游标移动方向
        wrapper.orderBy(cursorColumn, order);

        // 设置游标条件
        if (StrUtil.isNotBlank(request.getCursor())) {
            if (order) {
                // 升序
                wrapper.gt(cursorColumn, parseCursor(request.getCursor(), cursorType));
            } else {
                // 降序
                wrapper.lt(cursorColumn, parseCursor(request.getCursor(), cursorType));
            }
        }

        // 设置每次多取一条数据，用于判断是否是最后一页
        Page pageReq = request.initPage();
        pageReq.setPageSize(request.getPageSize() + 1);
        Page<T> page = mapper.page(pageReq, wrapper);

        // 获取数据，判断是否最后一页
        boolean isLast = true;
        if (page.getRecords().size() > request.getPageSize()) {
            // 如果查询结果大于 pageSize，则表示还有更多数据
            isLast = false;
            // 截取前 pageSize 条数据
            page.getRecords().remove(page.getRecords().size() - 1);
        }

        // 取出最后一条数据当作游标
        String cursor = Optional.ofNullable(CollectionUtil.getLast(page.getRecords()))
                .map(lastRecord -> BeanUtil.getProperty(lastRecord, LambdaUtil.getFieldName(cursorColumn)))
                .map(CursorUtils::toCursor)
                .orElse(null);


        return new CursorPageBaseResp<>(cursor, isLast, page.getRecords());
    }

    /**
     * 将游标对象转换为游标字符串 如果对象是Date类型，则将其转换为毫秒时间戳的字符串形式 </br>
     * 否则，直接调用对象的toString方法转换为字符串
     *
     * @param o 游标对象
     * @return 游标字符串
     */
    private static String toCursor(Object o) {
        if (o instanceof LocalDateTime) {
            return String.valueOf(((LocalDateTime) o).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        } else {
            return o.toString();
        }
    }

    /**
     * 解析游标字符串为指定类型的对象，若对象类型为LocalDateTime，则将其解析为毫秒时间戳
     *
     * @param cursor      游标字符串
     * @param cursorClass 游标所属的类
     * @return 游标对象
     */
    private static Object parseCursor(String cursor, Class<?> cursorClass) {
        if (LocalDateTime.class.isAssignableFrom(cursorClass)) {
            long epochMilli = Long.parseLong(cursor);
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMilli), ZoneId.systemDefault());
        } else {
            return cursor;
        }
    }
}
