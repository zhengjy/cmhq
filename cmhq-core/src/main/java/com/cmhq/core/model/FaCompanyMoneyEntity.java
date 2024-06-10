package com.cmhq.core.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;


/**
 * Created by Jiyang.Zheng on 2024/4/9 19:44.
 */
@TableName("fa_company_money")
@Data
public class FaCompanyMoneyEntity {
    /**
     * <pre>
     *
     * </pre>
     */
    @TableId(type = IdType.AUTO)
    private Integer	id;

    /**
     * <pre>
     *
     * </pre>
     */
    private Integer	cid;
    @TableField(exist = false)
    private String	companyName;
    /**
     * 分销人名称
     */
    @TableField(exist = false)
    private String	faUsername;

    /**
     * <pre>
     * 金额
     * </pre>
     */
    private Double	money;

    /**
     * <pre>
     * 1系统2充值3消费
     * </pre>
     */
    private Integer	type;

    /**
     * <pre>
     * 1增肌2减少
     * </pre>
     */
    private Integer	addType;

    /**
     * <pre>
     * 具体信息
     * </pre>
     */
    private String	msg;

    /**
     * <pre>
     * 操作之前余额
     * </pre>
     */
    @TableField(value = "`before`")
    private Double	before;

    /**
     * <pre>
     *
     * </pre>
     */
    private Date createTime;

    /**
     * <pre>
     * 2发货实际扣费
     * </pre>
     */
    private Integer	koufeiType;

    /**
     * <pre>
     * 0:未删除,1:已删除
     * </pre>
     */
    private Integer	delkid;

    /**
     * <pre>
     * 0:未删除,1:已删除
     * </pre>
     */
    private Integer	xdelkid;

    /**
     * <pre>
     * 订单ID 作废，因为历史只能存入数字
     * </pre>
     */
    @Deprecated
    private String	orderid;
    private String	orderNo;

    /**
     * <pre>
     * 运单号
     * </pre>
     */
    @TableField( "billCode")
    private String	billCode;

    /**
     * <pre>
     * 操作之后余额
     * </pre>
     */
    @TableField(value = "`after`")
    private Double	after;
}
