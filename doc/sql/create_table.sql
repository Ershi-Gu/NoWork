-- 创建数据库
create database if not exists no_work;
use no_work;

-- 热榜信息表
create table if not exists hot_board
(
    id              bigint(20)                              not null auto_increment primary key comment '热榜id',
    name            varchar(255)                            null comment '热榜名称',
    type            varchar(255)                            null comment '数据源类型',
    type_name       varchar(255)                            null comment '数据源类型中文名',
    icon_url        varchar(255)                            null comment '热榜图标url',
    data_json       mediumtext                              null comment '数据-json格式',
    category        int                                     null comment '热榜分类',
    update_interval decimal(7, 2) default 0.50              null comment '更新时间间隔，小时单位',
    sort            int           default 0                 not null comment '展示排序，0最大',
    create_time     datetime      default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time     datetime      default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete       tinyint       default 0                 not null comment '是否删除：0-否，1-是',
    index idx_sort (is_delete, sort)
) engine = InnoDB
  character set utf8mb4
  collate utf8mb4_unicode_ci comment '热榜信息表';

-- 用户表
create table if not exists user
(
    id              bigint(20)                         not null auto_increment primary key comment '用户id',
    account         varchar(255)                       not null comment '用户账号',
    password        varchar(255)                       not null comment '用户密码',
    name            varchar(255)                       null comment '用户名',
    wx_open_id      varchar(32)                        null comment '微信open_id',
    email           varchar(255)                       null comment '绑定邮箱',
    avatar_url      varchar(255)                       null comment '用户头像url',
    profile         varchar(255)                       null comment '用户简介',
    active_status   tinyint(1)                         null comment '0-离线，1-在线',
    last_login_time datetime                           null comment '最后登录时间',
    ip_info         varchar(255)                       null comment 'ip解析信息',
    create_time     datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time     datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete       tinyint  default 0                 not null comment '是否删除：0-否，1-是',
    unique index idx_account (account),
    unique index idx_wx_open_id (wx_open_id),
    unique index idx_email (email),
    index index_active_status_last_login_time (active_status, last_login_time)
) engine = InnoDB
  character set utf8mb4
  collate utf8mb4_unicode_ci comment '用户表';

-- 好友表
create table if not exists user_friend
(
    id          bigint(20)                         not null auto_increment primary key comment '好友id',
    uid         bigint(20)                         not null comment '用户id',
    fid         bigint(20)                         not null comment '好友id',
    create_time datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete   tinyint  default 0                 not null comment '是否删除：0-否，1-是',
    index idx_uid_fid (uid, fid)
) engine = InnoDB
  character set utf8mb4
  collate utf8mb4_unicode_ci comment '好友表';

-- 用户申请表，用于加好友以及加群聊
create table if not exists user_apply
(
    id          bigint(20)                         not null auto_increment primary key comment '申请id',
    uid         bigint(20)                         not null comment '用户id',
    target_id   bigint(20)                         not null comment '目标用户id/目标群聊id',
    type        int                                not null comment '申请类型 1加好友 2加群聊',
    msg         varchar(255)                       null comment '申请信息',
    status      int                                not null default 0 comment '申请状态，0-待审批，1-已同意，2-已拒绝',
    read_status tinyint                            not null default 0 comment '0-未读，1-已读',
    create_time datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete   tinyint  default 0                 not null comment '是否删除：0-否，1-是',
    index idx_uid_target_id (uid, target_id),
    index idx_target_id_read_status (target_id, read_status)
) engine = InnoDB
  character set utf8mb4
  collate utf8mb4_unicode_ci comment '用户申请表';

-- 房间表
create table if not exists room
(
    id          bigint(20)                         not null auto_increment primary key comment '房间id',
    type        int                                not null comment '房间类型，0-全员群，1-单聊，2-群聊',
    hot_flag    tinyint                            not null default 0 comment '是否是热点群聊，0-非热点，1-热点',
    active_time datetime                           null comment '会话最后活跃时间',
    last_msg_id bigint(20)                         null comment '最后一条消息id',
    create_time datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete   tinyint  default 0                 not null comment '是否删除：0-否，1-是'
) engine = InnoDB
  character set utf8mb4
  collate utf8mb4_unicode_ci comment '房间表';

-- 单聊房间表
create table if not exists room_friend
(
    id          bigint(20)                         not null auto_increment primary key comment '单聊房间id',
    room_id     bigint(20)                         not null comment '房间id',
    uid1        bigint(20)                         not null comment '用户1id',
    uid2        bigint(20)                         not null comment '用户2id',
    room_key    varchar(32)                        not null comment '房间key，由两个uid拼接，uid1_uid2（uid1小）',
    status      tinyint                            not null default 0 comment '0-正常，1-禁用',
    create_time datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete   tinyint  default 0                 not null comment '是否删除：0-否，1-是',
    unique index idx_room_key (room_key)
) engine = InnoDB
  character set utf8mb4
  collate utf8mb4_unicode_ci comment '单聊房间表';

-- 群聊房间表
create table if not exists room_group
(
    id          bigint(20)                         not null auto_increment primary key comment '群聊房间id',
    room_id     bigint(20)                         not null comment '房间id',
    name        varchar(255)                       not null comment '群名',
    avatar_url  varchar(255)                       not null comment '群头像',
    ext_json    json comment '群扩展信息',
    create_time datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete   tinyint  default 0                 not null comment '是否删除：0-否，1-是',
    index idx_name (name)
) engine = InnoDB
  character set utf8mb4
  collate utf8mb4_unicode_ci comment '群聊房间表';