package com.ershi.common.utils;

import com.alibaba.fastjson2.*;

import java.util.List;

/**
 * 基于fastjson2的json工具类
 *
 * @author Ershi-Gu.
 * @since 2025-09-18
 */
public class JsonUtils {

    /**
     * 字符串转对象
     */
    public static <T> T toObj(String str, Class<T> clz) {
        try {
            return JSON.parseObject(str, clz);
        } catch (JSONException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    /**
     * 字符串转对象（支持泛型）
     */
    public static <T> T toObj(String str, TypeReference<T> typeReference) {
        try {
            return JSON.parseObject(str, typeReference);
        } catch (JSONException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    /**
     * 字符串转 List
     */
    public static <T> List<T> toList(String str, Class<T> clz) {
        try {
            return JSON.parseArray(str, clz);
        } catch (JSONException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    /**
     * 字符串转 JSONObject
     */
    public static JSONObject toJSONObject(String str) {
        try {
            return JSON.parseObject(str, JSONObject.class);
        } catch (JSONException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    /**
     * JSONObject 转对象
     */
    public static <T> T nodeToValue(JSONObject node, Class<T> clz) {
        try {
            return JSON.to(clz, node);
        } catch (JSONException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    /**
     * 对象转字符串
     */
    public static String toStr(Object t) {
        try {
            return JSON.toJSONString(t);
        } catch (Exception e) {
            throw new UnsupportedOperationException(e);
        }
    }
}
