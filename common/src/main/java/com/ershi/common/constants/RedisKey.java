package com.ershi.common.constants;

/**
 * 管理Redis中存放的key
 *
 * @author Ershi
 * @date 2024/11/29
 */
public class RedisKey {

    /** 项目基础key */
    private static final String BASE_KEY = "noWork:";

    /** 请求唯一id */
    public static final String REQUEST_ID_KEY = "requestId:%s";

    /**
     * redisson限流器key
     */
    public static final String REDISSON_RATE_LIMITER_KEY = "redissonRateLimiter:%s";

    /**
     * anji-captcha
     */
    public static final String ANJI_CAPTCHA_KEY = "captcha:%s";

    /**
     * 热榜数据列表
     */
    public static final String HOT_BOARD_LIST_KEY = "hotBoardList";

    /**
     * 邮箱注册验证码
     */
    public static final String REGISTER_EMAIL_CAPTCHA_KEY = "registerEmailCaptcha:%s";

    /**
     * 获取key
     *
     * @param key 带有模板字符串的key
     * @param o   填入模板的参数
     * @return {@link String} 业务前缀key + 参数
     */
    public static String getKey(String key, Object... o) {
        return BASE_KEY + String.format(key, o);
    }
}
