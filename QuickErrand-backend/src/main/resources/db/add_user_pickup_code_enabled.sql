-- 为用户表添加收货码默认开关字段
-- 用户可以设置是否默认开启收货码
ALTER TABLE t_user ADD COLUMN pickup_code_enabled INT DEFAULT 1 COMMENT '是否默认开启收货码（0关闭1开启）';

-- 修改订单表的收货码字段允许为空（当用户关闭收货码时，该字段为空）
ALTER TABLE t_order MODIFY COLUMN pickup_code VARCHAR(10) DEFAULT NULL COMMENT '收货码（可为空，当用户关闭收货码时为null）';
