-- 更新管理员密码
-- 密码：admin123
-- BCrypt加密后的密码
UPDATE t_user 
SET password = '$2a$10$vd0rYQmJ2pPY.fOIukswAOt0frwWjrI6ZYS6N4UlSBYMmq/kqTLD.' 
WHERE username = 'admin';
