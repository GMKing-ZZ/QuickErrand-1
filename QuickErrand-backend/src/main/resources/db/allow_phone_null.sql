-- 修改 t_user 表，允许 phone 字段为 NULL
-- 因为微信登录用户可能没有绑定手机号

ALTER TABLE `t_user` 
MODIFY COLUMN `phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '手机号';

-- 注意：由于 phone 字段有唯一索引，需要先删除唯一索引，然后重新创建（允许 NULL 值）
-- MySQL 的唯一索引允许多个 NULL 值

-- 删除原有的唯一索引
ALTER TABLE `t_user` DROP INDEX `uk_phone`;

-- 重新创建唯一索引（允许 NULL 值）
ALTER TABLE `t_user` ADD UNIQUE INDEX `uk_phone`(`phone` ASC) USING BTREE;
