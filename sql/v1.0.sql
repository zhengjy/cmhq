ALTER TABLE sys_user ADD platform varchar(50) DEFAULT 'admin' NULL COMMENT 'admin：管理端，商户端：company';


INSERT INTO sys_role (name,`level`,description,data_scope,create_by,update_by,create_time,update_time) VALUES
    ('商户',2,'-','本级',NULL,'admin','2018-11-23 13:09:06.0','2024-04-05 13:54:46.0')
;

INSERT INTO sys_dept (pid,sub_count,name,dept_sort,enabled,create_by,update_by,create_time,update_time) VALUES
    (2,0,'商户',999,1,'admin','admin','2020-08-02 14:49:07.0','2020-08-02 14:49:07.0')
;

CREATE TABLE `fa_field` (
                            `id` int(11) NOT NULL AUTO_INCREMENT,
                            `business_type` varchar(255) DEFAULT NULL COMMENT '业务类型：sto_order,jitu_order',
                            `pid` int(11) DEFAULT NULL COMMENT '父id',
                            `name` varchar(255) DEFAULT NULL COMMENT '字段名称',
                            `is_null` varchar(255) DEFAULT NULL COMMENT '是否允许为空 Y、N',
                            `field_type` varchar(255) DEFAULT NULL COMMENT '字段类型：string,int,array,double',
                            `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                            PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT '字段配置';

ALTER TABLE sys_user ADD company_Id int(11) NULL COMMENT '商户id';


CREATE TABLE `fa_upload_log` (
                                    `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                    `upload_type` varchar(255) COMMENT '上传类型',
                                    `upload_code` text COMMENT '上传唯一标识',
                                    `msg` text COMMENT '返回日志',
                                    `error_msg` text COMMENT '错误日志+完整返回json',
                                    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                    `upload_token` varchar(255) DEFAULT NULL,
                                    `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',
                                    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT '上传日志';

CREATE TABLE `fa_courier_company` (
                                      `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                      `courier_code` varchar(255) COMMENT '快递公司编码：如sto：申通',
                                      `courier_name` varchar(255) COMMENT '名称',
                                      `api_url` varchar(255) COMMENT 'apiurl',
                                      `token_info` varchar(2000) COMMENT '认证信息',
                                      PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT '快递公司';

CREATE TABLE `fa_courier_order_ext` (
                                        `courier_order_id` int(11) NOT NULL ,
                                        `cname` varchar(255) COMMENT '名稱',
                                        `cvalue` varchar(255) COMMENT '值',
                                        PRIMARY KEY (`id`),
                                        UNIQUE KEY `idx_order_id` (`cellcode`,`cname`) USING HASH
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COMMENT '快递订单扩展数据';


CREATE TABLE `fa_courier_order` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `fa_company_id` int(11) DEFAULT NULL COMMENT '商户id',
    `order_no` varchar(255) DEFAULT NULL COMMENT '订单号',
    `courier_order_no` varchar(255) DEFAULT NULL COMMENT '快递公司订单号',
    `courier_company_waybill_no` varchar(255) DEFAULT NULL COMMENT '快递公司运单号',
    `goods_type` varchar(255) DEFAULT NULL COMMENT '物品分类',
    `goods_name` varchar(255) DEFAULT NULL COMMENT '物品名称',
    `weight` double(10,2) DEFAULT NULL COMMENT '重量',
    `width` int(10) DEFAULT NULL COMMENT '宽度',
    `length` int(10) DEFAULT NULL COMMENT '长度',
    `height` int(10) DEFAULT NULL COMMENT '高',
    `estimate_price` decimal(10,2) DEFAULT '0.00' COMMENT '商品金额',
    `from_name` varchar(255) DEFAULT NULL COMMENT '发货人',
    `from_mobile` varchar(255) DEFAULT NULL COMMENT '发货手机号',
    `from_prov` varchar(255) DEFAULT NULL COMMENT '发出省份',
    `from_city` varchar(255) DEFAULT NULL COMMENT '发出城市',
    `from_area` varchar(255) DEFAULT NULL COMMENT '发出区县',
    `from_address`` varchar(255) DEFAULT NULL COMMENT '发出地址',
    `to_name` varchar(255) DEFAULT NULL COMMENT '收货人',
  `to_mobile` varchar(255) DEFAULT NULL COMMENT '收货手机号',
  `to_prov` varchar(255) DEFAULT NULL COMMENT '收货省份',
  `to_city` varchar(255) DEFAULT NULL COMMENT '收货城市',
  `to_area` varchar(255) DEFAULT NULL COMMENT '收货区县',
  `to_address` varchar(255) NOT NULL COMMENT '收货地址',
  `is_jiesuan` tinyint(1) DEFAULT '0' COMMENT '0未结算1已结算',
  `cancel_order_state` int(1) NOT NULL DEFAULT '0' COMMENT '-1:待审核,0取消待审核1正常2审核通过3审核不通过',
  `order_state` int(1) NOT NULL DEFAULT '0' COMMENT '1运输中2派件中3已签收',
  `create_user` varchar(255) DEFAULT NULL COMMENT '创建人',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `update_user` varchar(255) DEFAULT NULL COMMENT '更新人',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT '快递订单信息';

ALTER TABLE fa_company MODIFY COLUMN name varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL;


CREATE TABLE `sys_childuser` (
	`id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `zi_max_num` int(10) DEFAULT '0' COMMENT '子账号一天最大单量',
  `zi_max_money` decimal(10,2) DEFAULT '0.00' COMMENT '子账号每日最大订单金额',
  `zi_one_max_money` decimal(10,2) DEFAULT '0.00' COMMENT '子账号单笔订单最大限制金额',
  `child_company_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商户子账户'

ALTER TABLE sys_childuser ADD child_company_id INT NULL;


 ALTER TABLE fa_recharge ADD apply_trade_no varchar(200) NULL COMMENT '支付平台订单号';


ALTER TABLE fa_expressorder ADD courier_company_code varchar(100) NULL COMMENT '快递公司编码。如sto';
ALTER TABLE fa_expressorder CHANGE courier_company_code courier_company_code varchar(100) NULL COMMENT '快递公司编码。如sto' AFTER orderid;
ALTER TABLE fa_expressorder ADD create_user_id varchar(100) NULL COMMENT '创建人id';
ALTER TABLE fa_expressorder ADD courier_company_order_no varchar(100) NULL COMMENT '快递公司订单编号';
ALTER TABLE fa_expressorder CHANGE courier_company_order_no courier_company_order_no varchar(100) NULL COMMENT '快递公司订单编号' AFTER courier_company_code;

 ALTER TABLE fa_company ADD estimate_price DOUBLE NULL COMMENT '冻结金额';


 ALTER TABLE fa_expressorder MODIFY COLUMN jitu_wuliu int(1) DEFAULT  NULL COMMENT '1运输中2派件中3已签收';
ALTER TABLE fa_expressorder MODIFY COLUMN qj_time varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '取件时间';
ALTER TABLE fa_expressorder MODIFY COLUMN qs_time varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '签收时间';
ALTER TABLE fa_expressorder MODIFY COLUMN cancel_type int(1) DEFAULT 0 NULL COMMENT '1自己取消2极兔取消';
ALTER TABLE fa_expressorder MODIFY COLUMN error_msg varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '极兔的异常信息';

ALTER TABLE fa_company_money ADD order_no varchar(500) NULL COMMENT '支付订单号';
