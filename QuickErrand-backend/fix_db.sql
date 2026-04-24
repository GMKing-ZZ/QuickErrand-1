-- 修复屏蔽跑腿员功能缺失的表
-- 执行此脚本前请确保已连接到 quick_errand 数据库

-- 创建跑腿员黑名单表（如果不存在）
CREATE TABLE IF NOT EXISTS `t_runner_blacklist` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID（拉黑者）',
  `runner_id` BIGINT NOT NULL COMMENT '跑腿员ID（被拉黑者）',
  `reason` VARCHAR(255) DEFAULT NULL COMMENT '拉黑原因',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除标记（0-未删除，1-已删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_runner` (`user_id`, `runner_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_runner_id` (`runner_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='跑腿员黑名单表';

-- 创建轮播图表（如果不存在）
CREATE TABLE IF NOT EXISTS `t_banner` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '轮播图ID',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '轮播图标题',
  `image_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '图片URL',
  `link_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '跳转链接（可选）',
  `sort_order` int NOT NULL DEFAULT 0 COMMENT '排序（数字越小越靠前）',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态（0禁用1启用）',
  `position` tinyint NOT NULL DEFAULT 1 COMMENT '展示位置（1首页2个人中心）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_position`(`position` ASC) USING BTREE,
  INDEX `idx_sort_order`(`sort_order` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '轮播图表' ROW_FORMAT = Dynamic;

-- 创建聊天删除记录表（如果不存在）
CREATE TABLE IF NOT EXISTS `t_chat_delete_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID（执行删除操作的用户）',
  `contact_id` bigint NOT NULL COMMENT '联系人ID',
  `order_id` bigint DEFAULT NULL COMMENT '订单ID（可选，删除特定订单会话时使用）',
  `delete_time` bigint NOT NULL COMMENT '删除时间戳（毫秒）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_contact` (`user_id`, `contact_id`),
  KEY `idx_user_contact_order` (`user_id`, `contact_id`, `order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='聊天删除记录表';

-- 添加用户表缺失的字段（如果不存在）
ALTER TABLE t_user ADD COLUMN IF NOT EXISTS pickup_code_enabled INT DEFAULT 1;
ALTER TABLE t_order MODIFY COLUMN IF EXISTS pickup_code VARCHAR(10) DEFAULT NULL;
