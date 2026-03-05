-- 添加启用收货码字段
ALTER TABLE t_order ADD COLUMN enable_pickup_code INT DEFAULT 1 COMMENT '是否启用收货码（1启用，0禁用）';