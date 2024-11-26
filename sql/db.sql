create database live;

use live;

create table user
(
    id                 varchar(10) primary key comment '用户 id',
    nickName           varchar(20)                            not null comment '昵称',
    email              varchar(150)                           not null comment '邮箱',
    password           varchar(50)                            not null comment '密码',
    sex                tinyint(1) comment '0:女 1:男 2:未知',
    birthday           varchar(10) comment '出生日期',
    school             varchar(150) comment '学校',
    personIntroduction varchar(120) comment '个人简介',
    lastLoginTime      datetime comment '最后登录时间',
    lastLoginIp        varchar(15) comment '最后登录 IP',
    userRole           varchar(256) default 'user'            not null comment '用户角色：user/admin/ban',
    noticeInfo         varchar(300) comment '空间公告',
    totalCoinCount     int                                    not null comment '硬币总数量',
    currentCoinCount   int                                    not null comment '当前硬币总数量',
    theme              tinyint(1)                             not null default 1 comment '主题',
    createTime         datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime         datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete           tinyint(1)   default 0                 not null comment '是否删除',
    unique index idx_key_email (email),
    unique index idx_nick_name (nickName)
) comment '用户表';

create table category
(
    id               int primary key auto_increment not null comment '分类 id',
    code             varchar(30)                    not null comment '分类编码',
    name             varchar(30)                    not null comment '分类名称',
    parentCategoryId int                            not null comment '父级分类 id',
    icon             varchar(50) default null comment '图标',
    background       varchar(50) default null comment '背景图',
    sort             tinyint(4)                     not null comment '排序号',
    unique index idx_key_code (code) using BTREE
) comment '分类表';