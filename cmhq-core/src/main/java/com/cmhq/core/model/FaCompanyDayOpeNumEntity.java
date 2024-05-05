package com.cmhq.core.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 商户操作数
 */
@TableName("fa_company_day_ope_num2")
@Data
public class FaCompanyDayOpeNumEntity {
    /**订单取消数*/
    public static final String TYPE_ORDER_CANCEL_NUM = "order_cancel_num";
    /**订单取消金额*/
    public static final String TYPE_ORDER_CANCEL_MAX_MONEY = "order_cancel_max_money";
    /**订单创建数(最大单量)*/
    public static final String TYPE_ORDER_CREATE_NUM = "order_create_num";
    /**订单最大金额(最大金额)*/
    public static final String TYPE_ORDER_CREATE_MAX_MONEY = "order_create_max_money";
    /**检验下单重量和结算重量单位百 触发次数  order_create_num/ check_weight_ratio_num  得到触发率*/
    public static final String TYPE_CHECK_WEIGHT_RATIO_NUM = "check_weight_ratio_num";
    /**消费金额*/
    public static final String TYPE_CONSUME_MONEY = "consume_money";

    @TableId(type = IdType.AUTO)
    private Integer	id;

    /**操作日期*/
    private String	opeDate;
    /**操作类型：order_cancel_num：订单取消数,order_create_num：订单创建数,consume_money：消费金额*/
    private String	opeType;
    /**商户id*/
    private Integer	companyId;
    /**上级 商户id*/
    private Integer	parentCompanyId;
    /**操作值*/
    private Double	opeValue;

}
