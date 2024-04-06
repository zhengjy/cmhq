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
