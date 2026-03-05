/*
 插入轮播图测试数据
 Date: 2026-02-01
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- 清空现有数据（可选，根据需要决定是否执行）
-- DELETE FROM `t_banner`;

-- 插入三个首页轮播图测试数据
-- 注意：image_url 字段需要在实际使用时通过管理端上传图片后获取，这里使用占位符
-- 用户可以在管理端轮播图管理中上传实际图片来替换这些占位符URL
INSERT INTO `t_banner` (`title`, `image_url`, `link_url`, `sort_order`, `status`, `position`, `create_time`, `update_time`) VALUES
('快速送达', 'http://localhost:8088/api/uploads/placeholder/banner1.jpg', '', 1, 1, 1, NOW(), NOW()),
('安全可靠', 'http://localhost:8088/api/uploads/placeholder/banner2.jpg', '', 2, 1, 1, NOW(), NOW()),
('价格透明', 'http://localhost:8088/api/uploads/placeholder/banner3.jpg', '', 3, 1, 1, NOW(), NOW());

SET FOREIGN_KEY_CHECKS = 1;
