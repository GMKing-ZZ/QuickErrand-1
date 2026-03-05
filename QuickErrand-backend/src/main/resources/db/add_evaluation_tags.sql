-- 为评价表添加标签字段
-- 执行时间: 2026-03-03
-- 描述: 添加 tags 字段用于存储评价标签（JSON数组格式）

ALTER TABLE `t_evaluation` ADD COLUMN `tags` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '评价标签（JSON数组）' AFTER `images`;
