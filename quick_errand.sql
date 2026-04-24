/*
 Navicat Premium Dump SQL

 Source Server         : 本地数据库
 Source Server Type    : MySQL
 Source Server Version : 80044 (8.0.44)
 Source Host           : localhost:3306
 Source Schema         : quick_errand

 Target Server Type    : MySQL
 Target Server Version : 80044 (8.0.44)
 File Encoding         : 65001

 Date: 15/04/2026 11:46:12
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
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '地址表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_address
-- ----------------------------
INSERT INTO `t_address` VALUES (7, 15, '李超', '19216278622', '山东省', '临沂市', '兰陵县', '代村幸福小区203', 118.036402, 34.839739, 1, '2026-03-04 17:44:58', '2026-03-04 17:44:58');
INSERT INTO `t_address` VALUES (8, 15, '李超', '19216278622', '山东省', '临沂市', '兰陵县', '卞庄街道银座花园', 118.074696, 34.854979, 0, '2026-04-01 22:26:00', '2026-04-01 22:26:00');

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
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '公告表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_announcement
-- ----------------------------
INSERT INTO `t_announcement` VALUES (1, '跑腿小程序正式开张', '<p>跑腿小程序正式开张、新用户优惠</p>', 1, 0, 1, NULL, 0, '2026-01-31 17:17:15', '2026-02-13 18:30:49');

-- ----------------------------
-- Table structure for t_banner
-- ----------------------------
DROP TABLE IF EXISTS `t_banner`;
CREATE TABLE `t_banner`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '轮播图ID',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '轮播图标题',
  `image_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '图片URL',
  `link_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '跳转链接（可选）',
  `sort_order` int NOT NULL DEFAULT 0 COMMENT '排序（数字越小越靠前）',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态（0禁用1启用）',
  `position` tinyint NOT NULL DEFAULT 1 COMMENT '展示位置（1首页2个人中心）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_position`(`position` ASC) USING BTREE,
  INDEX `idx_sort_order`(`sort_order` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '轮播图表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_banner
-- ----------------------------
INSERT INTO `t_banner` VALUES (1, '快速送达', 'http://localhost:8088/api/uploads/banners/20260304/b78be920604248c5a23bcdde586347f2.png', '', 1, 1, 1, '2026-02-01 15:41:20', '2026-03-04 18:30:28');
INSERT INTO `t_banner` VALUES (2, '安全可靠', 'http://localhost:8088/api/uploads/banners/20260304/e3eb17acca0648a1a13296b9cba359cb.png', '', 2, 1, 1, '2026-02-01 15:41:20', '2026-03-04 18:30:34');
INSERT INTO `t_banner` VALUES (3, '价格透明', 'http://localhost:8088/api/uploads/banners/20260304/f3bfd06ef1f246d3ba4c1eaed852cc8c.png', '', 3, 1, 1, '2026-02-01 15:41:20', '2026-03-04 18:30:51');

-- ----------------------------
-- Table structure for t_chat_delete_record
-- ----------------------------
DROP TABLE IF EXISTS `t_chat_delete_record`;
CREATE TABLE `t_chat_delete_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `contact_id` bigint NOT NULL,
  `order_id` bigint NULL DEFAULT NULL,
  `delete_time` bigint NOT NULL,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_contact`(`user_id` ASC, `contact_id` ASC) USING BTREE,
  INDEX `idx_user_contact_order`(`user_id` ASC, `contact_id` ASC, `order_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_chat_delete_record
-- ----------------------------
INSERT INTO `t_chat_delete_record` VALUES (1, 21, 15, 27, 1772968353324, '2026-03-08 19:12:33', '2026-03-08 19:12:33');

-- ----------------------------
-- Table structure for t_chat_message
-- ----------------------------
DROP TABLE IF EXISTS `t_chat_message`;
CREATE TABLE `t_chat_message`  (
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
) ENGINE = InnoDB AUTO_INCREMENT = 118 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '聊天消息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_chat_message
-- ----------------------------
INSERT INTO `t_chat_message` VALUES (95, 26, 21, 15, '您的东西送到了', 1, 1, 1772617758681, 1772617954782, '2026-03-04 17:49:19', '2026-03-04 17:52:34', 0);
INSERT INTO `t_chat_message` VALUES (96, 26, 21, 15, 'http://localhost:8088/api/uploads/chat/26/e56f4ebe18cb4b9eaeb90e46374758b4.png', 2, 1, 1772617890290, 1772617954782, '2026-03-04 17:51:30', '2026-03-04 17:52:34', 0);
INSERT INTO `t_chat_message` VALUES (97, 26, 15, 21, '好的，谢谢', 1, 1, 1772617962952, 1772618093980, '2026-03-04 17:52:43', '2026-03-04 17:54:53', 0);
INSERT INTO `t_chat_message` VALUES (98, 26, 15, 21, 'http://localhost:8088/api/uploads/chat/26/955d823fa8d5417d8055626cb4688365.png', 2, 1, 1772618044116, 1772618093980, '2026-03-04 17:54:04', '2026-03-04 17:54:53', 0);
INSERT INTO `t_chat_message` VALUES (99, 27, 21, 15, 'http://localhost:8088/api/uploads/chat/27/69bae13cefc64133b02e2a8af7dc01d3.png', 2, 1, 1772622063154, 1772622136068, '2026-03-04 19:01:03', '2026-03-04 19:02:16', 0);
INSERT INTO `t_chat_message` VALUES (100, 27, 21, 15, '已送达，尽快取件哈', 1, 1, 1772622076287, 1772622136068, '2026-03-04 19:01:16', '2026-03-04 19:02:16', 0);
INSERT INTO `t_chat_message` VALUES (101, 27, 21, 15, '以防丢失', 1, 1, 1772622092831, 1772622136068, '2026-03-04 19:01:33', '2026-03-04 19:02:16', 0);
INSERT INTO `t_chat_message` VALUES (102, 27, 15, 21, '好的谢谢', 1, 1, 1772622145437, 1772623182013, '2026-03-04 19:02:25', '2026-03-04 19:19:42', 0);
INSERT INTO `t_chat_message` VALUES (103, 27, 15, 21, '1212', 1, 1, 1772963238550, 1772963280378, '2026-03-08 17:47:19', '2026-03-08 17:48:00', 0);
INSERT INTO `t_chat_message` VALUES (104, 27, 15, 21, '1212', 1, 1, 1772963240190, 1772963280378, '2026-03-08 17:47:20', '2026-03-08 17:48:00', 0);
INSERT INTO `t_chat_message` VALUES (105, 27, 15, 21, '1212', 1, 1, 1772963241288, 1772963280378, '2026-03-08 17:47:21', '2026-03-08 17:48:00', 0);
INSERT INTO `t_chat_message` VALUES (106, 27, 15, 21, '1212', 1, 1, 1772963242314, 1772963280378, '2026-03-08 17:47:22', '2026-03-08 17:48:00', 0);
INSERT INTO `t_chat_message` VALUES (107, 27, 15, 21, '434', 1, 1, 1772963782081, 1772963806253, '2026-03-08 17:56:22', '2026-03-08 17:56:46', 0);
INSERT INTO `t_chat_message` VALUES (108, 27, 15, 21, '34', 1, 1, 1772963783105, 1772963806253, '2026-03-08 17:56:23', '2026-03-08 17:56:46', 0);
INSERT INTO `t_chat_message` VALUES (109, 27, 15, 21, '3434', 1, 1, 1772963784806, 1772963806253, '2026-03-08 17:56:25', '2026-03-08 17:56:46', 0);
INSERT INTO `t_chat_message` VALUES (110, 27, 15, 21, '34343', 1, 1, 1772963786101, 1772963806253, '2026-03-08 17:56:26', '2026-03-08 17:56:46', 0);
INSERT INTO `t_chat_message` VALUES (111, 27, 21, 15, '45345', 1, 1, 1772963902919, 1772963931127, '2026-03-08 17:58:23', '2026-03-08 17:58:51', 0);
INSERT INTO `t_chat_message` VALUES (112, 27, 21, 15, '534534', 1, 1, 1772963904503, 1772963931127, '2026-03-08 17:58:25', '2026-03-08 17:58:51', 0);
INSERT INTO `t_chat_message` VALUES (113, 27, 21, 15, '435345', 1, 1, 1772963905690, 1772963931127, '2026-03-08 17:58:26', '2026-03-08 17:58:51', 0);
INSERT INTO `t_chat_message` VALUES (114, 27, 15, 21, '222222', 1, 1, 1772968404550, 1772968426003, '2026-03-08 19:13:25', '2026-03-08 19:13:46', 0);
INSERT INTO `t_chat_message` VALUES (115, 30, 15, 21, '在不', 1, 1, 1775054234286, 1775054255517, '2026-04-01 22:37:14', '2026-04-01 22:37:35', 0);
INSERT INTO `t_chat_message` VALUES (116, 30, 21, 15, '在的在的', 1, 1, 1775054268097, 1775469108629, '2026-04-01 22:37:48', '2026-04-06 17:51:48', 0);
INSERT INTO `t_chat_message` VALUES (117, 30, 15, 21, 'http://localhost:8088/api/uploads/chat/30/78e187c482d44871b1320ed6f7ceb9d2.png', 2, 0, 1775469265987, NULL, '2026-04-06 17:54:26', '2026-04-06 17:54:26', 0);

-- ----------------------------
-- Table structure for t_chat_order_rel
-- ----------------------------
DROP TABLE IF EXISTS `t_chat_order_rel`;
CREATE TABLE `t_chat_order_rel`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_id` bigint NOT NULL COMMENT '跑腿订单ID',
  `user_id` bigint NOT NULL COMMENT '下单用户ID',
  `runner_id` bigint NOT NULL COMMENT '跑腿员ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除（0-未删除，1-已删除）',
  `delete_time` datetime NULL DEFAULT NULL COMMENT '软删除时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_order_id`(`order_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_runner_id`(`runner_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 19 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '订单-聊天绑定表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_chat_order_rel
-- ----------------------------
INSERT INTO `t_chat_order_rel` VALUES (16, 26, 15, 21, '2026-03-04 17:48:45', '2026-03-04 17:48:45', 0, NULL);
INSERT INTO `t_chat_order_rel` VALUES (17, 27, 15, 21, '2026-03-04 18:59:12', '2026-03-04 18:59:12', 0, NULL);
INSERT INTO `t_chat_order_rel` VALUES (18, 30, 15, 21, '2026-04-01 22:37:10', '2026-04-01 22:37:10', 0, NULL);

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
) ENGINE = InnoDB AUTO_INCREMENT = 23 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '收益记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_earnings_record
-- ----------------------------
INSERT INTO `t_earnings_record` VALUES (18, 21, 26, 13.50, 1, 2, '订单完成，订单号：ORD20260304174638836116', '2026-03-04 17:55:04', '2026-03-04 18:01:56');
INSERT INTO `t_earnings_record` VALUES (19, 21, NULL, 13.50, 3, 2, '提现申请，提现金额：13.5元', '2026-03-04 18:02:39', '2026-03-04 18:02:39');
INSERT INTO `t_earnings_record` VALUES (20, 21, 27, 11.70, 1, 2, '订单完成，订单号：ORD20260304185836912371', '2026-03-04 19:01:42', '2026-03-04 19:07:10');
INSERT INTO `t_earnings_record` VALUES (21, 21, 30, 13.50, 1, 2, '订单完成，订单号：ORD20260401223014525078', '2026-04-01 22:42:21', '2026-04-01 22:56:16');
INSERT INTO `t_earnings_record` VALUES (22, 21, NULL, 25.20, 3, 2, '提现申请，提现金额：25.2元', '2026-04-01 22:57:05', '2026-04-01 22:57:05');

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
  `tags` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态（0正常1已删除）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_order_id`(`order_id` ASC) USING BTREE,
  INDEX `idx_runner_id`(`runner_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  CONSTRAINT `fk_evaluation_order` FOREIGN KEY (`order_id`) REFERENCES `t_order` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 18 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '评价表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_evaluation
-- ----------------------------
INSERT INTO `t_evaluation` VALUES (15, 26, 15, 21, 5, 5, '很快，很不错，点赞！', '[\"http://localhost:8088/api/uploads/reviews/26/8768ae1a09ec4ee09259389f175d1326.png\"]', '[\"服务态度好\",\"配送速度快\",\"物品完好\",\"准时送达\",\"沟通及时\",\"专业可靠\"]', 0, '2026-03-04 17:56:32', '2026-03-04 17:56:32');
INSERT INTO `t_evaluation` VALUES (16, 27, 15, 21, 5, 5, '很可靠，不错', '[\"http://localhost:8088/api/uploads/reviews/27/4e99501882d34e1b9eaebaf2a2928a6a.png\"]', '[\"服务态度好\",\"配送速度快\",\"物品完好\",\"准时送达\",\"沟通及时\",\"专业可靠\"]', 0, '2026-03-04 19:03:59', '2026-03-04 19:03:59');
INSERT INTO `t_evaluation` VALUES (17, 30, 15, 21, 5, 5, '', '[]', '[\"服务态度好\",\"配送速度快\"]', 0, '2026-04-01 22:44:06', '2026-04-01 22:44:06');

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
) ENGINE = InnoDB AUTO_INCREMENT = 64 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '消息通知表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_message
-- ----------------------------
INSERT INTO `t_message` VALUES (23, 15, '订单支付成功', '您的订单 ORD20260304174638836116 已支付成功，等待跑腿员接单', 1, 26, 1, '2026-03-04 17:46:44', '2026-03-04 17:46:53');
INSERT INTO `t_message` VALUES (24, 15, '跑腿员已接单', '您的订单 ORD20260304174638836116 已被 泡泡 接单，请耐心等待', 1, 26, 1, '2026-03-04 17:48:38', '2026-03-04 17:56:57');
INSERT INTO `t_message` VALUES (25, 15, '新消息', '您有一条来自 泡泡 的新消息，点击查看', 3, 26, 1, '2026-03-04 17:49:19', '2026-03-04 17:56:54');
INSERT INTO `t_message` VALUES (26, 15, '新消息', '您有一条来自 泡泡 的新消息，点击查看', 3, 26, 1, '2026-03-04 17:51:30', '2026-03-04 17:52:34');
INSERT INTO `t_message` VALUES (27, 21, '新消息', '您有一条来自 李超 的新消息，点击查看', 3, 26, 1, '2026-03-04 17:52:43', '2026-03-08 17:45:25');
INSERT INTO `t_message` VALUES (28, 21, '新消息', '您有一条来自 李超 的新消息，点击查看', 3, 26, 1, '2026-03-04 17:54:04', '2026-03-08 17:45:20');
INSERT INTO `t_message` VALUES (29, 15, '订单已完成', '您的订单 ORD20260304174638836116 已完成，请对跑腿员进行评价', 1, 26, 1, '2026-03-04 17:55:04', '2026-03-04 17:56:49');
INSERT INTO `t_message` VALUES (30, 21, '订单已完成', '您服务的订单 ORD20260304174638836116 已完成，收益 13.50 元', 1, 26, 1, '2026-03-04 17:55:04', '2026-03-04 17:57:48');
INSERT INTO `t_message` VALUES (31, 15, '订单支付成功', '您的订单 ORD20260304185836912371 已支付成功，等待跑腿员接单', 1, 27, 1, '2026-03-04 18:58:41', '2026-03-08 17:55:59');
INSERT INTO `t_message` VALUES (32, 15, '跑腿员已接单', '您的订单 ORD20260304185836912371 已被 泡泡 接单，请耐心等待', 1, 27, 1, '2026-03-04 18:59:08', '2026-03-08 17:56:06');
INSERT INTO `t_message` VALUES (33, 15, '新消息', '您有一条来自 泡泡 的新消息，点击查看', 3, 27, 1, '2026-03-04 19:01:03', '2026-03-08 17:55:53');
INSERT INTO `t_message` VALUES (34, 15, '新消息', '您有一条来自 泡泡 的新消息，点击查看', 3, 27, 1, '2026-03-04 19:01:16', '2026-03-08 17:47:03');
INSERT INTO `t_message` VALUES (35, 15, '新消息', '您有一条来自 泡泡 的新消息，点击查看', 3, 27, 1, '2026-03-04 19:01:33', '2026-03-06 03:32:37');
INSERT INTO `t_message` VALUES (36, 15, '订单已完成', '您的订单 ORD20260304185836912371 已完成，请对跑腿员进行评价', 1, 27, 1, '2026-03-04 19:01:42', '2026-03-06 12:34:33');
INSERT INTO `t_message` VALUES (37, 21, '订单已完成', '您服务的订单 ORD20260304185836912371 已完成，收益 11.70 元', 1, 27, 1, '2026-03-04 19:01:42', '2026-03-04 19:19:55');
INSERT INTO `t_message` VALUES (38, 21, '新消息', '您有一条来自 李超 的新消息，点击查看', 3, 27, 1, '2026-03-04 19:02:25', '2026-03-04 19:19:53');
INSERT INTO `t_message` VALUES (39, 15, '订单支付成功', '您的订单 ORD20260308163646133550 已支付成功，等待跑腿员接单', 1, 28, 1, '2026-03-08 16:37:04', '2026-03-08 17:46:20');
INSERT INTO `t_message` VALUES (40, 15, '订单已取消', '您的订单 ORD20260308163646133550 已取消，原因：用户取消', 1, 28, 1, '2026-03-08 16:37:20', '2026-03-08 17:46:10');
INSERT INTO `t_message` VALUES (42, 21, '新消息', '您有一条来自 李超 的新消息，点击查看', 3, 27, 1, '2026-03-08 17:47:19', '2026-03-08 17:48:23');
INSERT INTO `t_message` VALUES (43, 21, '新消息', '您有一条来自 李超 的新消息，点击查看', 3, 27, 1, '2026-03-08 17:47:20', '2026-03-08 17:48:18');
INSERT INTO `t_message` VALUES (44, 21, '新消息', '您有一条来自 李超 的新消息，点击查看', 3, 27, 1, '2026-03-08 17:47:21', '2026-03-08 17:48:12');
INSERT INTO `t_message` VALUES (45, 21, '新消息', '您有一条来自 李超 的新消息，点击查看', 3, 27, 1, '2026-03-08 17:47:22', '2026-03-08 17:48:09');
INSERT INTO `t_message` VALUES (46, 21, '新消息', '您有一条来自 李超 的新消息，点击查看', 3, 27, 1, '2026-03-08 17:56:22', '2026-03-08 17:57:05');
INSERT INTO `t_message` VALUES (47, 21, '新消息', '您有一条来自 李超 的新消息，点击查看', 3, 27, 1, '2026-03-08 17:56:23', '2026-03-08 17:57:02');
INSERT INTO `t_message` VALUES (50, 15, '新消息', '您有一条来自 泡泡 的新消息，点击查看', 3, 27, 1, '2026-03-08 17:58:23', '2026-03-08 17:59:14');
INSERT INTO `t_message` VALUES (51, 15, '新消息', '您有一条来自 泡泡 的新消息，点击查看', 3, 27, 1, '2026-03-08 17:58:25', '2026-03-08 17:59:07');
INSERT INTO `t_message` VALUES (52, 15, '新消息', '您有一条来自 泡泡 的新消息，点击查看', 3, 27, 1, '2026-03-08 17:58:26', '2026-03-08 17:59:02');
INSERT INTO `t_message` VALUES (54, 21, '新消息', '您有一条来自 李超 的新消息，点击查看', 3, 27, 1, '2026-03-08 19:13:25', '2026-03-08 19:14:08');
INSERT INTO `t_message` VALUES (55, 15, '订单支付成功', '您的订单 ORD20260401223014525078 已支付成功，等待跑腿员接单', 1, 30, 0, '2026-04-01 22:30:59', '2026-04-01 22:30:59');
INSERT INTO `t_message` VALUES (56, 15, '跑腿员已接单', '您的订单 ORD20260401223014525078 已被 泡泡 接单，请耐心等待', 1, 30, 0, '2026-04-01 22:34:56', '2026-04-01 22:34:56');
INSERT INTO `t_message` VALUES (57, 21, '新消息', '您有一条来自 李超 的新消息，点击查看', 3, 30, 0, '2026-04-01 22:37:14', '2026-04-01 22:37:14');
INSERT INTO `t_message` VALUES (58, 15, '新消息', '您有一条来自 泡泡 的新消息，点击查看', 3, 30, 0, '2026-04-01 22:37:48', '2026-04-01 22:37:48');
INSERT INTO `t_message` VALUES (59, 15, '订单已完成', '您的订单 ORD20260401223014525078 已完成，请对跑腿员进行评价', 1, 30, 0, '2026-04-01 22:42:21', '2026-04-01 22:42:21');
INSERT INTO `t_message` VALUES (60, 21, '订单已完成', '您服务的订单 ORD20260401223014525078 已完成，收益 13.50 元', 1, 30, 1, '2026-04-01 22:42:21', '2026-04-06 13:27:08');
INSERT INTO `t_message` VALUES (61, 15, '订单支付成功', '您的订单 ORD20260401230641422378 已支付成功，等待跑腿员接单', 1, 31, 0, '2026-04-01 23:06:46', '2026-04-01 23:06:46');
INSERT INTO `t_message` VALUES (62, 15, '跑腿员已接单', '您的订单 ORD20260401230641422378 已被 李华 接单，请耐心等待', 1, 31, 0, '2026-04-01 23:36:47', '2026-04-01 23:36:47');
INSERT INTO `t_message` VALUES (63, 21, '新消息', '您有一条来自 李超 的新消息，点击查看', 3, 30, 0, '2026-04-06 17:54:26', '2026-04-06 17:54:26');

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
  `pickup_code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
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
) ENGINE = InnoDB AUTO_INCREMENT = 32 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '订单表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_order
-- ----------------------------
INSERT INTO `t_order` VALUES (26, 'ORD20260304174638836116', 15, 21, 1, '丽华', '19276789344', '山东省临沂市兰陵县卞庄街道兰陵县监察委员会兰陵县人民政府', 118.070720, 34.857290, '山东省临沂市兰陵县代村幸福小区203', '李超', '19216278622', 118.036402, 34.839739, 3689, '水果', '[\"http://tmp/1wNZ99Tx3XKv24f161abce41b1d2515ab8ece4a03a65.png\"]', NULL, 15.00, 1.50, 13.50, '无', NULL, 4, 1, '2026-03-04 17:46:44', '2026-03-04 17:48:38', NULL, '2026-03-04 17:55:04', NULL, NULL, '2026-03-04 17:46:38', '2026-03-04 17:46:38');
INSERT INTO `t_order` VALUES (27, 'ORD20260304185836912371', 15, 21, 3, '李华', '19287656783', '山东省临沂市兰陵县卞庄街道开元乐家购物广场兰陵文化公园', 118.069446, 34.855302, '山东省临沂市兰陵县代村幸福小区203', '李超', '19216278622', 118.036402, 34.839739, 3476, '书', '[\"http://localhost:8088/api/uploads/order-items/pending/20260304/b5b7e07929e846dbaebfbb64fb60128d.png\"]', NULL, 13.00, 1.30, 11.70, '没有', NULL, 4, 1, '2026-03-04 18:58:41', '2026-03-04 18:59:08', NULL, '2026-03-04 19:01:42', NULL, NULL, '2026-03-04 18:58:37', '2026-03-04 18:58:37');
INSERT INTO `t_order` VALUES (30, 'ORD20260401223014525078', 15, 21, 1, '李华', '19876567237', '山东省临沂市兰陵县卞庄街道莲花山城莲花山城中区', 118.047608, 34.867675, '山东省临沂市兰陵县卞庄街道银座花园', '李超', '19216278622', 118.074696, 34.854979, 2846, '苹果', '[\"http://localhost:8088/api/uploads/order-items/pending/20260401/16ed98c319eb46db8f386c166375636b.jpg\"]', NULL, 15.00, 1.50, 13.50, '', '128530', 4, 1, '2026-04-01 22:30:59', '2026-04-01 22:34:56', NULL, '2026-04-01 22:42:21', NULL, NULL, '2026-04-01 22:30:14', '2026-04-01 22:30:14');
INSERT INTO `t_order` VALUES (31, 'ORD20260401230641422378', 15, 61, 1, '张三', '19267536787', '山东省临沂市兰陵县卞庄街道开元乐家购物广场兰陵文化公园', 118.069446, 34.855302, '山东省临沂市兰陵县卞庄街道银座花园', '李超', '19216278622', 118.074696, 34.854979, 480, '蛋糕', '[\"http://localhost:8088/api/uploads/order-items/pending/20260401/13fe2276bb464d0d928cff32305a519e.jpg\"]', NULL, 11.50, 1.15, 10.35, '', '585264', 3, 1, '2026-04-01 23:06:46', '2026-04-01 23:36:47', NULL, NULL, NULL, NULL, '2026-04-01 23:06:42', '2026-04-01 23:06:42');

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
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '订单类型表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_order_type
-- ----------------------------
INSERT INTO `t_order_type` VALUES (1, '代买', '/static/icons/daimai.png', '帮您购买商品', 1, 1, '2026-01-25 17:27:14', '2026-02-01 11:39:49');
INSERT INTO `t_order_type` VALUES (2, '代送', '/static/icons/daisong.png', '帮您配送物品', 2, 1, '2026-01-25 17:27:14', '2026-02-01 11:39:51');
INSERT INTO `t_order_type` VALUES (3, '代取', '/static/icons/daiqu.png', '帮您取件取货', 3, 1, '2026-01-25 17:27:14', '2026-02-01 11:39:53');
INSERT INTO `t_order_type` VALUES (4, '代办', '/static/icons/daiban.png', '帮您办理事务', 4, 1, '2026-01-25 17:27:14', '2026-01-25 17:27:14');
INSERT INTO `t_order_type` VALUES (5, '代寄', '/static/icons/daiji.png', '帮您寄送快递', 5, 1, '2026-01-25 17:27:14', '2026-01-25 17:27:14');

-- ----------------------------
-- Table structure for t_runner_blacklist
-- ----------------------------
DROP TABLE IF EXISTS `t_runner_blacklist`;
CREATE TABLE `t_runner_blacklist`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID（拉黑者）',
  `runner_id` bigint NOT NULL COMMENT '跑腿员ID（被拉黑者）',
  `reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '拉黑原因',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除标记（0-未删除，1-已删除）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_runner`(`user_id` ASC, `runner_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_runner_id`(`runner_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '跑腿员黑名单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_runner_blacklist
-- ----------------------------
INSERT INTO `t_runner_blacklist` VALUES (1, 15, 21, NULL, '2026-03-08 17:02:07', '2026-04-06 19:04:26', 1);

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
  `credit_level` tinyint NOT NULL DEFAULT 5 COMMENT '信用等级（1-5星）',
  `total_orders` int NOT NULL DEFAULT 0 COMMENT '完成订单数',
  `good_rate` decimal(5, 2) NOT NULL DEFAULT 0.00 COMMENT '好评率',
  `service_time` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '服务时间',
  `service_range` int NULL DEFAULT 5000 COMMENT '服务范围（米）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_cert_status`(`cert_status` ASC) USING BTREE,
  INDEX `idx_credit_level`(`credit_level` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '跑腿员信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_runner_info
-- ----------------------------
INSERT INTO `t_runner_info` VALUES (5, 21, '泡泡', '371424199009106413', 'http://localhost:8088/api/uploads/runner-auth/paopao/d6a344ccd1e94a239bbbe214465707ea.png', 'http://localhost:8088/api/uploads/runner-auth/paopao/7a0ab7acc90944f4a6c40a800a0e5730.png', 2, NULL, '', 1, 1, 100.00, '08:00-22:00', 10000, '2026-03-04 19:31:30', '2026-03-03 13:48:55');
INSERT INTO `t_runner_info` VALUES (10, 61, '李华', '371424198807066541', 'http://localhost:8088/api/uploads/runner-auth/user9747616404/df768bd740c5473fbac2e5f78e709fda.png', 'http://localhost:8088/api/uploads/runner-auth/user9747616404/e4e71162623b425ca70ac0449a7e2e5c.png', 2, NULL, '', 3, 0, 100.00, NULL, 5000, '2026-04-01 23:35:48', '2026-04-01 23:34:02');

-- ----------------------------
-- Table structure for t_user
-- ----------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `openid` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '微信openid',
  `phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '手机号',
  `username` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '账号',
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
  `pickup_code_enabled` int NULL DEFAULT 1,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_openid`(`openid` ASC) USING BTREE,
  UNIQUE INDEX `uk_phone`(`phone` ASC) USING BTREE,
  INDEX `idx_user_type`(`user_type` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 62 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_user
-- ----------------------------
INSERT INTO `t_user` VALUES (1, NULL, '13800000000', 'admin', '$2a$10$qNuRrSQ/LdAMomgq7Jidieske6aOwO4hYQDYxA3g6kqkXtXblgFua', '系统管理员', 'http://localhost:8088/api/uploads/common/20260304/bfb453e6d1404b7784e544867d1d473e.png', 0, NULL, 3, 1, 0.00, '2026-01-29 22:52:48', '2026-01-29 23:04:29', 0, 1);
INSERT INTO `t_user` VALUES (15, NULL, '19216278622', 'mincer233', '$2a$10$wpbA3BSjYTfdu0n.7XnWROfOAMkS0St2.g992ISisWq4tm4DF01Ou', '李超', 'http://localhost:8088/api/uploads/avatars/user/a889ad6476704a459985fea9f5f2e874.jpg', 1, '2026-03-04', 1, 1, 151.12, '2026-02-28 14:32:00', '2026-02-28 14:32:00', 0, 0);
INSERT INTO `t_user` VALUES (21, NULL, '18751189111', 'paopao', '$2a$10$fPxq3TkLH37l87Br0HShe.E19S4c6HoW8HnYrmDdzPOp3oYtzqVqG', '泡泡', 'http://localhost:8088/api/uploads/avatars/user/2dece4d455214e198418bef983f6cf47.png', 1, '2004-03-07', 2, 1, 0.00, '2026-03-03 13:47:41', '2026-03-03 13:47:41', 0, 1);
INSERT INTO `t_user` VALUES (60, 'mock_openid_1775055150610', NULL, 'wx_55150610', '$2a$10$j4B9xylF/2nMJWNyuMjubu..ECSVX8N.dWCsQYxYLH.AwruLdFJCi', '微信用户1775055150613', NULL, 0, NULL, 1, 1, 0.00, '2026-04-01 22:52:31', '2026-04-01 22:52:31', 0, 1);
INSERT INTO `t_user` VALUES (61, NULL, '15069974761', 'user9747616404', '$2a$10$X.GInkuSH6zQotGbn5cSLePgaCbbDrUh.uolXUggAVZ/DNO.GGjUO', '李华', NULL, 0, NULL, 2, 1, 0.00, '2026-04-01 22:59:20', '2026-04-01 22:59:20', 0, 1);

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
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '提现记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_withdrawal_record
-- ----------------------------
INSERT INTO `t_withdrawal_record` VALUES (6, 21, 13.50, 0.14, 13.36, 2, 'mincer233', 4, '2026-03-04 18:02:47', '2026-03-04 18:02:52', NULL, '2026-03-04 18:02:39', '2026-03-04 18:02:39');
INSERT INTO `t_withdrawal_record` VALUES (7, 21, 25.20, 0.25, 24.95, 2, 'mincer2333', 1, NULL, NULL, NULL, '2026-04-01 22:57:05', '2026-04-01 22:57:05');

SET FOREIGN_KEY_CHECKS = 1;
