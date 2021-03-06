CREATE TABLE `lt_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(30) DEFAULT NULL,
  `password` varchar(30) DEFAULT NULL,
  `phone` varchar(11) DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

CREATE TABLE `lt_realmarket` (
  `id` bigint(12) NOT NULL AUTO_INCREMENT,
  `stock_name` varchar(10) DEFAULT NULL,
  `stock_code` varchar(8) DEFAULT NULL,
  `avg_price` double(10,2) DEFAULT NULL,
  `deal_date` varchar(12) DEFAULT NULL COMMENT '成交日期',
  `deal_time` varchar(15) DEFAULT NULL,
  `time_minute` varchar(15) DEFAULT NULL,
  `rose` varchar(8) DEFAULT NULL,
  `deal_num_sum` varchar(20) DEFAULT NULL,
  `deal_rmb_sum` varchar(20) DEFAULT NULL,
  `exchange` varchar(8) DEFAULT NULL,
  `volamount` int(6) DEFAULT NULL,
  `repeat_ratio` double(8,4) DEFAULT NULL COMMENT '成交量重复比例',
  `duration` int(4) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `lt_clinch_info` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `sotck_code` varchar(8) DEFAULT NULL,
  `deal_time` varchar(16) DEFAULT NULL,
  `capital_in` double(16,2) DEFAULT NULL,
  `capital_out` double(16,2) DEFAULT NULL,
  `net_inflow` double(16,2) DEFAULT NULL,
  `big_capital_in` double(16,2) DEFAULT NULL,
  `big_capital_out` double(16,2) DEFAULT NULL,
  `big_net_inflow` double(16,2) DEFAULT NULL,
  `net_inflow_pct` double(16,2) DEFAULT NULL,
  `redo_pct` double(8,4) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `lt_capital_info` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `stock_code` char(8) DEFAULT NULL,
  `stock_name` varchar(16) DEFAULT NULL,
  `deal_time` varchar(16) DEFAULT NULL,
  `capital_size` double(16,2) DEFAULT NULL,
  `rose` double(8,4) DEFAULT NULL,
  `exchange` varchar(255) DEFAULT NULL,
  `voturnover` int(8) DEFAULT NULL,
  `circulation_cap` double(16,2) DEFAULT NULL,
  `mkt_cap` double(16,2) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `code_index` (`stock_code`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;