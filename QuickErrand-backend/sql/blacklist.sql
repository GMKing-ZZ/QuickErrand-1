-- 跑腿员黑名单表
CREATE TABLE IF NOT EXISTS `t_runner_blacklist` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID（拉黑者）',
  `runner_id` BIGINT NOT NULL COMMENT '跑腿员ID（被拉黑者）',
  `reason` VARCHAR(255) DEFAULT NULL COMMENT '拉黑原因',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除标记（0-未删除，1-已删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_runner` (`user_id`, `runner_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_runner_id` (`runner_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='跑腿员黑名单表';
