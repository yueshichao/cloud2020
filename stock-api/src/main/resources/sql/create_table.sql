drop database if exists stock;
create database stock;

use stock;

-- 库存快照表
drop table if exists `t_stock`;
CREATE TABLE `t_stock`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `stock` int(10) NOT NULL DEFAULT 0 COMMENT '库存',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4;

INSERT INTO `stock`.`t_stock`(`id`, `stock`) VALUES (1, 10000);

-- 库存流水表
drop table if exists `t_sku_serial`;
CREATE TABLE `t_sku_serial` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `serial_id` varchar(255) CHARACTER SET utf8mb4 NOT NULL DEFAULT '' COMMENT '流水id',
  `type` varchar(255) CHARACTER SET utf8mb4 NOT NULL DEFAULT '' COMMENT '类型：1- 扣减，2 - 回滚',
  `sku_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE = InnoDB CHARACTER SET = utf8mb4;