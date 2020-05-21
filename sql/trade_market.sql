/*
 Navicat Premium Data Transfer

 Source Server         : 127.0.0.1
 Source Server Type    : MySQL
 Source Server Version : 80020
 Source Host           : 127.0.0.1:3306
 Source Schema         : trade_market

 Target Server Type    : MySQL
 Target Server Version : 80020
 File Encoding         : 65001

 Date: 21/05/2020 16:40:36
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for tm_trade_order
-- ----------------------------
DROP TABLE IF EXISTS `tm_trade_order`;
CREATE TABLE `tm_trade_order`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `TradeId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `UserId` int(0) NOT NULL,
  `TradePrice` decimal(10, 4) NOT NULL,
  `TradeCount` decimal(10, 4) NOT NULL,
  `TradeAmount` decimal(10, 4) NOT NULL,
  `SurplusCount` decimal(10, 4) NULL DEFAULT NULL,
  `SurplusAmount` decimal(10, 4) NULL DEFAULT NULL,
  `Buyer` int(0) NOT NULL,
  `TradeDate` datetime(0) NULL DEFAULT NULL,
  `FinishDate` datetime(0) NULL DEFAULT NULL,
  `MarketOrder` int(0) NOT NULL,
  `Cancel` int(0) NOT NULL,
  `CancelTime` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 23003 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tm_trade_transaction
-- ----------------------------
DROP TABLE IF EXISTS `tm_trade_transaction`;
CREATE TABLE `tm_trade_transaction`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `TransactionSN` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `SellerId` int(0) NULL DEFAULT NULL,
  `BuyerId` int(0) NULL DEFAULT NULL,
  `TradePrice` decimal(10, 4) NULL DEFAULT NULL,
  `TradeCount` decimal(10, 4) NULL DEFAULT NULL,
  `TradeAmount` decimal(10, 4) NULL DEFAULT NULL,
  `TradeTime` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 17804 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tm_user
-- ----------------------------
DROP TABLE IF EXISTS `tm_user`;
CREATE TABLE `tm_user`  (
  `UserId` int(0) NOT NULL AUTO_INCREMENT,
  `UserName` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `UserPass` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `UserMoney` decimal(20, 4) NULL DEFAULT NULL,
  `FreezeMoney` decimal(20, 4) NULL DEFAULT NULL,
  PRIMARY KEY (`UserId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tm_user
-- ----------------------------
INSERT INTO `tm_user` VALUES (1, 'haoge', '123456', 10000000.0000, 0.0000);
INSERT INTO `tm_user` VALUES (2, 'cc', '123456', 10000000.0000, 0.0000);
INSERT INTO `tm_user` VALUES (3, 'robot1', '123456', 10000000.0000, 0.0000);
INSERT INTO `tm_user` VALUES (4, 'robot2', '123456', 10000000.0000, 0.0000);

-- ----------------------------
-- Table structure for tm_user_good
-- ----------------------------
DROP TABLE IF EXISTS `tm_user_good`;
CREATE TABLE `tm_user_good`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `UserId` int(0) NOT NULL,
  `NiuCoin` decimal(20, 4) NOT NULL,
  `FreezeNiuCoin` decimal(20, 4) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tm_user_good
-- ----------------------------
INSERT INTO `tm_user_good` VALUES (1, 1, 100000.0000, 0.0000);
INSERT INTO `tm_user_good` VALUES (2, 2, 100000.0000, 0.0000);
INSERT INTO `tm_user_good` VALUES (3, 3, 100000.0000, 0.0000);
INSERT INTO `tm_user_good` VALUES (4, 4, 100000.0000, 0.0000);

SET FOREIGN_KEY_CHECKS = 1;
