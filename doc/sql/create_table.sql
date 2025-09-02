-- 热榜信息表
create table if not exists hot_board (
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
) engine = InnoDB character set utf8mb4 collate utf8mb4_unicode_ci comment '热榜信息表';