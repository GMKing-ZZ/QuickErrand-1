/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80025 (8.0.25)
 Source Host           : localhost:3306
 Source Schema         : quick_errand

 Target Server Type    : MySQL
 Target Server Version : 80025 (8.0.25)
 File Encoding         : 65001

 Date: 29/01/2026 19:16:57
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_address
-- ----------------------------
DROP TABLE IF EXISTS `t_address`;
CREATE TABLE `t_address`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '地址ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `contact_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '联系人姓名',
  `contact_phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '联系人电话',
  `province` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '省份',
  `city` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '城市',
  `district` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '区县',
  `detail_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '详细地址',
  `longitude` decimal(10, 6) NOT NULL COMMENT '经度',
  `latitude` decimal(10, 6) NOT NULL COMMENT '纬度',
  `is_default` tinyint NOT NULL DEFAULT 0 COMMENT '是否默认（0否1是）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '地址表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_announcement
-- ----------------------------
DROP TABLE IF EXISTS `t_announcement`;
CREATE TABLE `t_announcement`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '公告标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '公告内容',
  `position` tinyint NOT NULL DEFAULT 1 COMMENT '展示位置（1首页2个人中心）',
  `is_top` tinyint NOT NULL DEFAULT 0 COMMENT '是否置顶（0否1是）',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态（0草稿1已发布）',
  `publish_time` datetime NULL DEFAULT NULL COMMENT '发布时间',
  `read_count` int NOT NULL DEFAULT 0 COMMENT '阅读量',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_publish_time`(`publish_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '公告表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_earnings_record
-- ----------------------------
DROP TABLE IF EXISTS `t_earnings_record`;
CREATE TABLE `t_earnings_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `order_id` bigint NULL DEFAULT NULL COMMENT '订单ID',
  `amount` decimal(10, 2) NOT NULL COMMENT '金额',
  `type` tinyint NOT NULL COMMENT '类型（1订单收益2奖励3提现）',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态（1待结算2已结算）',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_order_id`(`order_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '收益记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_evaluation
-- ----------------------------
DROP TABLE IF EXISTS `t_evaluation`;
CREATE TABLE `t_evaluation`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '评价ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `runner_id` bigint NOT NULL COMMENT '跑腿员ID',
  `service_score` tinyint NOT NULL COMMENT '服务质量评分（1-5）',
  `attitude_score` tinyint NOT NULL COMMENT '服务态度评分（1-5）',
  `content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '评价内容',
  `images` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '评价图片（JSON数组）',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态（0正常1已删除）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_order_id`(`order_id` ASC) USING BTREE,
  INDEX `idx_runner_id`(`runner_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '评价表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_message
-- ----------------------------
DROP TABLE IF EXISTS `t_message`;
CREATE TABLE `t_message`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息内容',
  `type` tinyint NOT NULL COMMENT '消息类型（1订单消息2系统消息3聊天消息）',
  `related_id` bigint NULL DEFAULT NULL COMMENT '关联ID（订单ID等）',
  `is_read` tinyint NOT NULL DEFAULT 0 COMMENT '是否已读（0未读1已读）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_is_read`(`is_read` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '消息通知表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for chat_message
-- ----------------------------
DROP TABLE IF EXISTS `chat_message`;
CREATE TABLE `chat_message`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '消息唯一ID',
  `order_id` bigint NOT NULL COMMENT '关联跑腿订单ID',
  `from_user_id` bigint NOT NULL COMMENT '发送方用户ID',
  `to_user_id` bigint NOT NULL COMMENT '接收方用户ID',
  `content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息内容（已过滤敏感词）',
  `msg_type` tinyint NOT NULL DEFAULT 1 COMMENT '消息类型，1-文字',
  `read_status` tinyint NOT NULL DEFAULT 0 COMMENT '已读状态，0-未读，1-已读',
  `send_time` bigint NOT NULL COMMENT '消息发送时间戳（毫秒）',
  `read_time` bigint NULL DEFAULT NULL COMMENT '消息已读时间戳（毫秒）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除（0-未删除，1-已删除）',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_order_id`(`order_id` ASC) USING BTREE,
  INDEX `idx_to_user_read`(`to_user_id` ASC, `read_status` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '聊天消息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for chat_order_rel
-- ----------------------------
DROP TABLE IF EXISTS `chat_order_rel`;
CREATE TABLE `chat_order_rel`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_id` bigint NOT NULL COMMENT '跑腿订单ID',
  `user_id` bigint NOT NULL COMMENT '下单用户ID',
  `runner_id` bigint NOT NULL COMMENT '跑腿员ID',
  `delete_time` datetime NULL DEFAULT NULL COMMENT '软删除时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_order_id`(`order_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_runner_id`(`runner_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '订单-聊天绑定表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_order
-- ----------------------------
DROP TABLE IF EXISTS `t_order`;
CREATE TABLE `t_order`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '订单编号',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `runner_id` bigint NULL DEFAULT NULL COMMENT '跑腿员ID',
  `order_type_id` bigint NOT NULL COMMENT '订单类型ID',
  `pickup_contact` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '取货联系人',
  `pickup_phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '取货联系电话',
  `pickup_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '取货地址',
  `pickup_longitude` decimal(10, 6) NOT NULL COMMENT '取货经度',
  `pickup_latitude` decimal(10, 6) NOT NULL COMMENT '取货纬度',
  `delivery_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '收货地址',
  `delivery_contact` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '收货联系人',
  `delivery_phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '收货联系电话',
  `delivery_longitude` decimal(10, 6) NOT NULL COMMENT '收货经度',
  `delivery_latitude` decimal(10, 6) NOT NULL COMMENT '收货纬度',
  `distance` int NOT NULL COMMENT '距离（米）',
  `item_description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '物品描述',
  `item_images` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '物品图片（JSON数组）',
  `expected_time` datetime NULL DEFAULT NULL COMMENT '预期完成时间',
  `service_fee` decimal(10, 2) NOT NULL COMMENT '服务费用',
  `platform_fee` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '平台费用',
  `runner_fee` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '跑腿员费用',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `pickup_code` varchar(6) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '收货码',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '订单状态（1待支付2待接单3服务中4已完成5已取消）',
  `payment_method` tinyint NULL DEFAULT NULL COMMENT '支付方式（1微信支付2余额支付）',
  `pay_time` datetime NULL DEFAULT NULL COMMENT '支付时间',
  `accept_time` datetime NULL DEFAULT NULL COMMENT '接单时间',
  `pickup_time` datetime NULL DEFAULT NULL COMMENT '取货时间',
  `complete_time` datetime NULL DEFAULT NULL COMMENT '完成时间',
  `cancel_time` datetime NULL DEFAULT NULL COMMENT '取消时间',
  `cancel_reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '取消原因',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_order_no`(`order_no` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_runner_id`(`runner_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '订单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_order_type
-- ----------------------------
DROP TABLE IF EXISTS `t_order_type`;
CREATE TABLE `t_order_type`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '类型ID',
  `type_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '类型名称',
  `type_icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '类型图标',
  `type_desc` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '类型描述',
  `sort_order` int NOT NULL DEFAULT 0 COMMENT '排序',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态（0禁用1启用）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_sort_order`(`sort_order` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '订单类型表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_runner_info
-- ----------------------------
DROP TABLE IF EXISTS `t_runner_info`;
CREATE TABLE `t_runner_info`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '真实姓名',
  `id_card` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '身份证号',
  `id_card_front` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '身份证正面照',
  `id_card_back` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '身份证反面照',
  `cert_status` tinyint NOT NULL DEFAULT 0 COMMENT '认证状态（0未认证1审核中2已认证3已驳回）',
  `cert_time` datetime NULL DEFAULT NULL COMMENT '认证时间',
  `reject_reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '驳回原因',
  `credit_level` tinyint NOT NULL DEFAULT 3 COMMENT '信用等级（1-5星）',
  `total_orders` int NOT NULL DEFAULT 0 COMMENT '完成订单数',
  `good_rate` decimal(5, 2) NOT NULL DEFAULT 100.00 COMMENT '好评率',
  `service_time` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '服务时间',
  `service_range` int NULL DEFAULT 5000 COMMENT '服务范围（米）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_cert_status`(`cert_status` ASC) USING BTREE,
  INDEX `idx_credit_level`(`credit_level` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '跑腿员信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_user
-- ----------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `openid` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '微信openid',
  `phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '手机号',
  `password` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '密码（加密）',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '头像URL',
  `gender` tinyint NULL DEFAULT 0 COMMENT '性别（0未知1男2女）',
  `birthday` date NULL DEFAULT NULL COMMENT '生日',
  `user_type` tinyint NOT NULL DEFAULT 1 COMMENT '用户类型（1普通用户2跑腿员）',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态（0禁用1正常）',
  `balance` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '账户余额',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除（0否1是）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_phone`(`phone` ASC) USING BTREE,
  UNIQUE INDEX `uk_openid`(`openid` ASC) USING BTREE,
  INDEX `idx_user_type`(`user_type` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_withdrawal_record
-- ----------------------------
DROP TABLE IF EXISTS `t_withdrawal_record`;
CREATE TABLE `t_withdrawal_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `amount` decimal(10, 2) NOT NULL COMMENT '提现金额',
  `fee` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '手续费',
  `actual_amount` decimal(10, 2) NOT NULL COMMENT '实际到账金额',
  `account_type` tinyint NOT NULL COMMENT '账户类型（1支付宝2微信3银行卡）',
  `account_info` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '账户信息',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态（1待审核2已通过3已驳回4已到账）',
  `audit_time` datetime NULL DEFAULT NULL COMMENT '审核时间',
  `transfer_time` datetime NULL DEFAULT NULL COMMENT '转账时间',
  `reject_reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '驳回原因',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '提现记录表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
