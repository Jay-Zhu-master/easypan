/*
 Navicat Premium Data Transfer

 Source Server         : root@localhost
 Source Server Type    : MySQL
 Source Server Version : 80034 (8.0.34-0ubuntu0.20.04.1)
 Source Host           : localhost:3306
 Source Schema         : eazypan

 Target Server Type    : MySQL
 Target Server Version : 80034 (8.0.34-0ubuntu0.20.04.1)
 File Encoding         : 65001

 Date: 06/10/2023 23:46:55
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for email_code
-- ----------------------------
DROP TABLE IF EXISTS `email_code`;
CREATE TABLE `email_code`  (
  `email` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '邮箱',
  `code` varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '编号',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `status` tinyint(1) NULL DEFAULT NULL COMMENT '0：未使用  1：已使用',
  PRIMARY KEY (`email`, `code`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '邮箱验证码' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_info
-- ----------------------------
DROP TABLE IF EXISTS `user_info`;
CREATE TABLE `user_info`  (
  `user_id` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户id',
  `nick_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '昵称',
  `email` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邮箱',
  `qq_open_id` varchar(35) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'qqOpenId',
  `qq_avatar` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'qq头像',
  `password` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '密码',
  `join_time` datetime NULL DEFAULT NULL COMMENT '注册时间',
  `last_login_time` datetime NULL DEFAULT NULL COMMENT '最后登录时间',
  `status` tinyblob NULL COMMENT '0 禁用 1 启用',
  `use_space` bigint NULL DEFAULT NULL COMMENT '使用空间',
  `total_space` bigint NULL DEFAULT NULL COMMENT '总空间',
  PRIMARY KEY (`user_id`) USING BTREE,
  UNIQUE INDEX `key_email`(`email` ASC) USING BTREE,
  UNIQUE INDEX `key_qq_open_id`(`qq_open_id` ASC) USING BTREE,
  UNIQUE INDEX `key_nick_name`(`nick_name` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户信息表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
