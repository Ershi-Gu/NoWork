package com.ershi.common.constants;

/**
 * 管理Redis中存放的key
 *
 * @author Ershi
 * @date 2024/11/29
 */
public class RedisKey {

    /**
     * 项目基础key
     */
    public static final String BASE_KEY = "noWork:";

    /**
     * 请求唯一id
     */
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
     * 登录用户信息缓存
     */
    public static final String LOGIN_USER_INFO_KEY = "login:userInfo:%d";

    /**
     * 在线用户表单
     */
    public static final String ONLINE_UID_ZET = "online:uid";

    /**
     * 离线用户表单
     */
    public static final String OFFLINE_UID_ZET = "offline:uid";

    /**
     * 房间信息
     */
    public static final String ROOM_KEY = "roomInfo:roomId_%d";

    /**
     * 群成员信息
     */
    public static final String ROOM_MEMBERS_BY_ROOM_ID_KEY = "roomMember:roomId_%d";

    /**
     * 通过groupId和uid获取群成员信息
     */
    public static final String ROOM_MEMBERS_BY_GROUP_ID_UID_KEY = "roomMember:groupUid_%d";

    /**
     * 单聊房间缓存信息
     */
    public static final String ROOM_FRIEND_KEY = "roomFriend:roomFriendId_%d";

    /**
     * 群聊房间信息
     */
    public static final String ROOM_GROUP_BY_ROOM_ID_KEY = "roomGroup:roomId_%d";

    /**
     * 热点会话活跃时间
     */
    public static final String HOT_ROOM_ACTIVE_TIME_KEY = "hotRoomActiveTime";

    /**
     * 全局唯一自增消息id
     */
    public static final String MSG_ID_KEY = "msgId";

    /**
     * 记录用户未ack消息id
     */
    public static final String MSG_USER_UN_ACK_KEY = "unack:uid_%d";

    /**
     * 消息简表，用于未ack情况下的重试等策略
     */
    public static final String MSG_INFO_KEY = "msg:id_%d";

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
