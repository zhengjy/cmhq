package com.cmhq.core.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName("fa_user_money")
@Data
public class FaUserMoneyEntity {
    @TableId(type = IdType.AUTO)
    private Integer	id;

    /**分校人id*/
    private Integer userId;
    @TableField(exist = false)
    private String userName;
    /**金额*/
    private Double money;
    /**=增加减少 1加2减*/
    @TableField(value = "`type`")
    private Integer type;
    /**备注*/
    private String msg;
    /**操作之前的余额*/
    @TableField(value = "`before`")
    private Double before;
    /**操作之前的余额*/
    private Double afterMoney;
    /**创建时间*/
    private String createTime;
    /**商户id*/
    private Integer cid;
    @TableField(exist = false)
    private String companyName;
    /**是否分销 0正常1分销*/
    private String isFenxiao;
    /**订单号*/
    private String orderid;
    /**运单号*/
    @TableField( "billCode")
    private String billCode;
}
