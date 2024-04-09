package com.cmhq.core.model;

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
    private Integer	id;

    /**
     * <pre>
     *
     * </pre>
     */
    private Integer	cid;

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
     * 订单ID
     * </pre>
     */
    private String	orderid;

    /**
     * <pre>
     * 极兔运单号
     * </pre>
     */
    private String	billCode;

    /**
     * <pre>
     * 操作之后余额
     * </pre>
     */
    private Double	after;
}
