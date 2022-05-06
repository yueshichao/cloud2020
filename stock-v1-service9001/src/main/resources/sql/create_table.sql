drop database if exists stock;
create database stock;

use stock;
CREATE TABLE `t_stock`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `stock` int(10) NULL DEFAULT NULL COMMENT '库存',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4;

INSERT INTO `stock`.`t_stock`(`id`, `stock`) VALUES (1, 10000);
