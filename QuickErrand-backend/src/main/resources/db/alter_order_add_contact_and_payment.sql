-- 为订单表补齐联系人/电话/支付方式字段（用于订单详情展示与数据回传）
-- 兼容已有数据：字段允许 NULL

ALTER TABLE `t_order`
  ADD COLUMN `pickup_contact` varchar(50) NULL DEFAULT NULL COMMENT '取货联系人' AFTER `order_type_id`,
  ADD COLUMN `pickup_phone` varchar(11) NULL DEFAULT NULL COMMENT '取货联系电话' AFTER `pickup_contact`,
  ADD COLUMN `delivery_contact` varchar(50) NULL DEFAULT NULL COMMENT '收货联系人' AFTER `delivery_address`,
  ADD COLUMN `delivery_phone` varchar(11) NULL DEFAULT NULL COMMENT '收货联系电话' AFTER `delivery_contact`,
  ADD COLUMN `payment_method` tinyint NULL DEFAULT NULL COMMENT '支付方式（1微信支付2余额支付）' AFTER `status`;

