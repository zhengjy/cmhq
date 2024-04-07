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
                                    `msg` text COMMENT '上传失败消息',
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