/*
 创建轮播图表
 Date: 2026-01-27
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '轮播图表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
