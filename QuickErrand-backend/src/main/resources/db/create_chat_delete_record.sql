-- 聊天删除记录表
-- 用于记录用户删除联系人或订单会话的时间点
-- 删除后，该用户无法看到删除前的聊天记录，对方不受影响

CREATE TABLE IF NOT EXISTS `t_chat_delete_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID（执行删除操作的用户）',
  `contact_id` bigint NOT NULL COMMENT '联系人ID',
  `order_id` bigint DEFAULT NULL COMMENT '订单ID（可选，删除特定订单会话时使用）',
  `delete_time` bigint NOT NULL COMMENT '删除时间戳（毫秒）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_contact` (`user_id`, `contact_id`),
  KEY `idx_user_contact_order` (`user_id`, `contact_id`, `order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='聊天删除记录表';
