package com.cmhq.core.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;


/**
 * 充值表
 * Created by Jiyang.Zheng on 2024/4/9 19:43.
 */
@TableName("fa_recharge")
@Data
public class FaRechargeEntity {
    /**
     * <pre>
     *
     * </pre>
     */
    @TableId(type = IdType.AUTO)
    private Integer	id;

    /**
     * <pre>
     * 公司ID
     * </pre>
     */
    private Integer	cid;
    @TableField(exist = false)
    private String companyName;
    /**
     * <pre>
     * 订单id
     * </pre>
     */
    private String	orderid;
    /**
     * 支付平台订单号
     */
    private String	applyTradeNo;

    /**
     * <pre>
     * 充值钱数
     * </pre>
     */
    private Double	money;

    /**
     * <pre>
     * 0未支付1已支付
     * </pre>
     */
    private Integer	status;

    /**
     * <pre>
     *
     * </pre>
     */
    private Date createTime;

    /**
     * <pre>
     * 支付时间
     * </pre>
     */
    private Date updateTime;

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
}
