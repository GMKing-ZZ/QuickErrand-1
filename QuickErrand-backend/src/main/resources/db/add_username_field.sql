-- 添加 username 字段到 t_user 表
-- 执行时间：2026-01-29

-- 1. 添加 username 字段
ALTER TABLE `t_user` 
ADD COLUMN `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户名' AFTER `openid`;

-- 2. 为 username 字段添加唯一索引
ALTER TABLE `t_user` 
ADD UNIQUE INDEX `uk_username`(`username` ASC) USING BTREE;

-- 3. 为现有用户生成 username（可选，如果需要为已有用户生成用户名）
-- 格式：user + 手机号后6位 + 4位随机数
-- UPDATE `t_user` SET `username` = CONCAT('user', SUBSTRING(`phone`, -6), FLOOR(1000 + RAND() * 9000)) WHERE `username` IS NULL;
