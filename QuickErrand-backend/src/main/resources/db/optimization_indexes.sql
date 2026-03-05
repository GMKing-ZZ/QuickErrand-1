-- 数据库性能优化索引
-- 创建日期：2026-01-27
-- 说明：为常用查询字段添加索引以提升查询性能

-- 用户表索引
CREATE INDEX IF NOT EXISTS idx_user_phone ON t_user(phone);
CREATE INDEX IF NOT EXISTS idx_user_status ON t_user(status);
CREATE INDEX IF NOT EXISTS idx_user_create_time ON t_user(create_time);

-- 跑腿员信息表索引
CREATE INDEX IF NOT EXISTS idx_runner_info_user_id ON t_runner_info(user_id);
CREATE INDEX IF NOT EXISTS idx_runner_info_cert_status ON t_runner_info(cert_status);
CREATE INDEX IF NOT EXISTS idx_runner_info_credit_level ON t_runner_info(credit_level);
CREATE INDEX IF NOT EXISTS idx_runner_info_cert_status_credit ON t_runner_info(cert_status, credit_level);

-- 订单表索引
CREATE INDEX IF NOT EXISTS idx_order_user_id ON t_order(user_id);
CREATE INDEX IF NOT EXISTS idx_order_runner_id ON t_order(runner_id);
CREATE INDEX IF NOT EXISTS idx_order_status ON t_order(status);
CREATE INDEX IF NOT EXISTS idx_order_order_no ON t_order(order_no);
CREATE INDEX IF NOT EXISTS idx_order_create_time ON t_order(create_time);
CREATE INDEX IF NOT EXISTS idx_order_runner_status ON t_order(runner_id, status);
CREATE INDEX IF NOT EXISTS idx_order_user_status ON t_order(user_id, status);

-- 订单类型表索引
CREATE INDEX IF NOT EXISTS idx_order_type_status ON order_type(status);

-- 地址表索引
CREATE INDEX IF NOT EXISTS idx_address_user_id ON address(user_id);
CREATE INDEX IF NOT EXISTS idx_address_is_default ON address(is_default);

-- 公告表索引
CREATE INDEX IF NOT EXISTS idx_announcement_status ON announcement(status);
CREATE INDEX IF NOT EXISTS idx_announcement_type ON announcement(type);
CREATE INDEX IF NOT EXISTS idx_announcement_is_top ON announcement(is_top);
CREATE INDEX IF NOT EXISTS idx_announcement_create_time ON announcement(create_time);
CREATE INDEX IF NOT EXISTS idx_announcement_status_top_sort ON announcement(status, is_top, sort);

-- 评价表索引
CREATE INDEX IF NOT EXISTS idx_order_review_order_id ON order_review(order_id);
CREATE INDEX IF NOT EXISTS idx_order_review_user_id ON order_review(user_id);
CREATE INDEX IF NOT EXISTS idx_order_review_runner_id ON order_review(runner_id);
CREATE INDEX IF NOT EXISTS idx_order_review_rating ON order_review(rating);
CREATE INDEX IF NOT EXISTS idx_order_review_status ON order_review(status);
CREATE INDEX IF NOT EXISTS idx_order_review_create_time ON order_review(create_time);
CREATE INDEX IF NOT EXISTS idx_order_review_runner_rating ON order_review(runner_id, rating);

-- 复合索引说明：
-- 1. idx_runner_info_cert_status_credit: 用于信用等级列表查询，同时过滤认证状态和信用等级
-- 2. idx_order_runner_status: 用于统计跑腿员完成订单数
-- 3. idx_order_user_status: 用于查询用户订单列表
-- 4. idx_announcement_status_top_sort: 用于公告列表排序查询
-- 5. idx_order_review_runner_rating: 用于统计跑腿员好评率
