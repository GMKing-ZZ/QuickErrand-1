-- 创建管理员账号
-- 执行时间：2026-01-29
-- 
-- 注意：密码字段需要使用 BCrypt 加密后的密文
-- 可以通过以下方式生成：
-- 1. 在项目中临时添加测试代码，使用 PasswordEncoder.encode("Admin@123456") 生成
-- 2. 或者使用在线 BCrypt 工具生成
--
-- 默认账号信息：
-- 用户名：admin
-- 手机号：13800000000
-- 密码：Admin@123456（需要替换为BCrypt密文）
-- 昵称：系统管理员

INSERT INTO `t_user` (
  `id`,
  `openid`,
  `username`,
  `phone`,
  `password`,
  `nickname`,
  `avatar`,
  `gender`,
  `birthday`,
  `user_type`,
  `status`,
  `balance`,
  `create_time`,
  `update_time`,
  `is_deleted`
) VALUES (
  NULL,                               -- id，自增
  NULL,                               -- openid
  'admin',                            -- username（管理员用户名）
  '13800000000',                      -- phone（管理员手机号）
  '<这里替换为BCrypt加密后的密码>',   -- password（BCrypt 密文，明文为：Admin@123456）
  '系统管理员',                        -- nickname
  NULL,                               -- avatar
  0,                                  -- gender（0未知）
  NULL,                               -- birthday
  3,                                  -- user_type（3管理员）
  1,                                  -- status（1正常）
  0.00,                               -- balance
  NOW(),                              -- create_time
  NOW(),                              -- update_time
  0                                   -- is_deleted（0未删除）
);

-- 如果用户名已存在，可以使用以下 SQL 更新现有用户为管理员
-- UPDATE `t_user` 
-- SET `username` = 'admin',
--     `user_type` = 3,
--     `status` = 1,
--     `password` = '<这里替换为BCrypt加密后的密码>'
-- WHERE `phone` = '13800000000' OR `username` = 'admin';
