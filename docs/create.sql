CREATE TABLE `user`
(
    `id`          bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '账号ID',
    `username`    varchar(50)  NOT NULL DEFAULT 'momo' COMMENT '用户名',
    `avatar_url`  varchar(255)          DEFAULT NULL COMMENT '头像图片url',
    `role`        tinyint      NOT NULL DEFAULT 10 COMMENT '角色(0-管理员,10-普通用户)',
    `password`    varchar(255) NOT NULL COMMENT '密码',
    `phone`       varchar(20) UNIQUE COMMENT '手机号',
    `create_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='账号表';


CREATE TABLE `article`
(
    `id`            bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '文章ID',
    `title`         varchar(255) NOT NULL COMMENT '标题',
    `cover`         varchar(120) NOT NULL DEFAULT '' COMMENT '文章封面',
    `author_id`     bigint(20) unsigned NOT NULL COMMENT '作者ID',
    `status`        tinyint               DEFAULT 1 COMMENT '状态（0-草稿，1-发布,2-下架,3-删除）',
    `view_count`    int                   DEFAULT 0 COMMENT '阅读量',
    `comment_count` int                   DEFAULT 0 COMMENT '评论数',
    `summary`       varchar(255)          DEFAULT NULL COMMENT '文章摘要',
    `create_time`   datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY             `idx_author_id` (`author_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章表';

CREATE TABLE `article_content`
(
    `id`          bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '文章内容id',
    `article_id`  bigint(20) unsigned NOT NULL COMMENT '文章id',
    `content`     text COMMENT '文章正文',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    KEY           `idx_article_id` (`article_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='文章内容表';

CREATE TABLE `tag`
(
    `id`          bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '标签id',
    `name`        varchar(60) NOT NULL DEFAULT '' COMMENT '标签名称',
    `create_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uk_name` (`name`) USING BTREE,
    KEY           `idx_create_time` (`create_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='文章标签表';

CREATE TABLE `article_tag_rel`
(
    `id`          bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
    `article_id`  bigint(20) unsigned NOT NULL COMMENT '文章ID',
    `tag_id`      bigint(20) unsigned NOT NULL COMMENT '标签ID',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY `uk_article_tag` (`article_id`, `tag_id`),
    KEY           `idx_tag_id` (`tag_id`),
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='文章标签关联表';


CREATE TABLE `category`
(
    `id`          bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '分类id',
    `name`        varchar(60) NOT NULL DEFAULT '' COMMENT '分类名称',
    `create_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uk_name` (`name`) USING BTREE,
    KEY           `idx_create_time` (`create_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='文章分类表';


CREATE TABLE `article_category_rel`
(
    `id`          bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
    `article_id`  bigint(20) unsigned NOT NULL COMMENT '文章id',
    `category_id` bigint(20) unsigned NOT NULL COMMENT '分类id',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uni_article_id` (`article_id`) USING BTREE,
    KEY           `idx_category_id` (`category_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='文章所属分类关联表';



CREATE TABLE `comment`
(
    `id`          bigint (20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
    `article_id`  bigint (20) unsigned NOT NULL COMMENT '关联的文章ID',
    `user_id`     bigint (20) unsigned NOT NULL COMMENT '发布者用户ID',
    `content`     varchar(255) NOT NULL DEFAULT '' COMMENT '评论内容',
    `create_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    KEY           `idx_article_id` (`article_id`) USING BTREE,
    KEY           `idx_user_id` (`user_id`) USING BTREE,
    KEY           `idx_create_time` (`create_time`) USING BTREE
) ENGINE = InnoDB COMMENT = '评论表';



CREATE TABLE `statistics_article_pv`
(
    `id`          bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
    `author_id`   bigint(20) unsigned NOT NULL COMMENT '作者ID',
    `pv_date`     date     NOT NULL COMMENT '被统计的日期',
    `pv_count`    bigint(20) unsigned NOT NULL COMMENT 'pv访问量',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uk_pv_date` (`pv_date`) USING BTREE,
    KEY           `idx_author_id` (`author_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='统计表 - 文章 PV (访问量)';



CREATE TABLE `chat_history`
(
    `id`           bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `session_id`   varchar(64) NOT NULL COMMENT '会话ID（用于多轮对话归组）',
    `user_id`      bigint(20) unsigned DEFAULT NULL COMMENT '用户ID',
    `role`         enum('user', 'assistant') NOT NULL COMMENT '说话角色（user用户, assistant助手）',
    `content`      text        NOT NULL COMMENT '消息内容',
    `model`        varchar(64)          DEFAULT NULL COMMENT 'LLM模型名称',
    `tokens`       int                  DEFAULT NULL COMMENT '该消息消耗token数',
    `created_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '消息创建时间',
    PRIMARY KEY (`id`),
    KEY            `idx_session_id` (`session_id`),
    KEY            `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='LLM对话历史记录表';