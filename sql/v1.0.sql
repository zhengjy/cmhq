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
                                        `courier_order_id` int(11) NOT NULL,
                                        `cname` varchar(100) NOT NULL DEFAULT '' COMMENT '名稱',
                                        `cvalue` varchar(255) DEFAULT NULL COMMENT '值',
                                        PRIMARY KEY (`courier_order_id`,`cname`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='快递订单扩展数据';



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

                                ALTER TABLE fa_expressorder ADD courier_wuliu_state varchar(50) NULL;
                                ALTER TABLE fa_expressorder MODIFY COLUMN courier_wuliu_state varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '物流公司返回物流信息状态';

CREATE TABLE `fa_company_day_ope_num2` (
                                          `id` int(11) NOT NULL AUTO_INCREMENT,
                                          `ope_date` varchar(255) DEFAULT NULL COMMENT '操作日期',
                                          `ope_type` varchar(255) DEFAULT NULL COMMENT '操作类型：order_cancel_num：订单取消数,order_create_num：订单创建数,consume_money：消费金额',
                                          `company_id` int(11) DEFAULT NULL COMMENT '商户id',
                                          `parent_company_Id` int(11) DEFAULT NULL COMMENT '上级商户id',
                                          `ope_value` double DEFAULT NULL COMMENT '操作值',
                                          PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT '商户操作数';

ALTER TABLE fa_company ADD is_freeze  varchar(20) DEFAULT NULL COMMENT '是否凍結：Y:是，N：否';
ALTER TABLE fa_company ADD check_weight_type varchar(100) DEFAULT NULL COMMENT '检验下单重量和结算重量周期类型：day，month，week';
ALTER TABLE fa_company ADD check_weight_num int(11) DEFAULT NULL COMMENT '检验下单重量和结算重量周期次数';
ALTER TABLE fa_company ADD check_weight_ratio int(11) DEFAULT NULL COMMENT '检验下单重量和结算重量单位百，130：就是百分之30';


ALTER TABLE sys_childuser ADD cancel_max_num int(11) DEFAULT NULL COMMENT '最多取消订单次数';
ALTER TABLE sys_childuser ADD cancel_max_money double DEFAULT NULL COMMENT '单笔取消订单金额上限';

ALTER TABLE fa_expressorder ADD courier_order_state varchar(100) DEFAULT NULL COMMENT '物流公司订单状态';

ALTER TABLE fa_expressorder MODIFY COLUMN jitu_status int(1) DEFAULT 1 NOT NULL COMMENT '1未调派业务员2已调派业务员3已揽收4已取件5已取消';

CREATE TABLE `fa_company_cost` (
                                   `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
                                   `pro_f` varchar(20) DEFAULT null COMMENT '始发省',
                                   `city_f` varchar(20) DEFAULT null COMMENT '始发地',
                                   `pro_d` varchar(20) DEFAULT null COMMENT '目的省',
                                   `city_d` varchar(20) DEFAULT null COMMENT '目的地',
                                   `address` varchar(20) DEFAULT null COMMENT '流向',
                                   `area` varchar(20) DEFAULT null COMMENT '区域',
                                   `price_init` decimal(10,3) unsigned DEFAULT '0.000' COMMENT '平台原价首重（1kg）',
                                   `price_init_to` decimal(10,3) unsigned DEFAULT '0.000' COMMENT '平台原价续重（元/kg）',
                                   `price` decimal(10,3) unsigned DEFAULT '0.000' COMMENT '首重（1kg）',
                                   `price_to` decimal(10,3) unsigned DEFAULT '0.000' COMMENT '续重（元/kg）',
                                   `courier_company_code` varchar(100) DEFAULT NULL COMMENT '快递公司编码。如sto',
                                   PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT COMMENT='快递公司成本价格';

CREATE TABLE `fa_courier_order_share_orderno` (
                                                  `order_no` varchar(50) NOT NULL,
                                                  PRIMARY KEY (`order_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='快递订单分享订单号';

ALTER TABLE fa_expressorder MODIFY COLUMN msg varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '备注';
ALTER TABLE fa_company ADD is_delete varchar(2) DEFAULT 'N' NULL COMMENT '是否删除Y/N';
ALTER TABLE sys_menu DROP KEY uniq_name;
ALTER TABLE sys_menu DROP KEY uniq_title;
ALTER TABLE fa_recharge ADD business_type int(2) DEFAULT 1 NULL COMMENT '业务类型: 1:充值,2:用户下单扣款';

ALTER TABLE fa_expressorder ADD take_goods_time_end varchar(100) NULL COMMENT '上门取件结束时间';

ALTER TABLE fa_product_category ADD categore_code varchar(100) NULL COMMENT '商品编码';
ALTER TABLE fa_product_category ADD `weight` double(10,2) DEFAULT NULL COMMENT '重量';
ALTER TABLE fa_product_category ADD `width` int(10) DEFAULT NULL COMMENT '宽度';
ALTER TABLE fa_product_category ADD `length` int(10) DEFAULT NULL COMMENT '长度';
ALTER TABLE fa_product_category ADD `height` int(10) DEFAULT NULL COMMENT '高';
ALTER TABLE fa_withdrawal MODIFY COLUMN `day` varchar(255) NULL COMMENT '提现日期';

ALTER TABLE fa_company ADD is_pay varchar(20) DEFAULT 'N' NULL COMMENT '是否开启去付款按钮：Y:是，N：否';
ALTER TABLE fa_company ADD pay_retio int(11) DEFAULT NULL COMMENT '去收款比例单位百';
