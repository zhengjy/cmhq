package com.cmhq.core.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName("fa_withdrawal")
@Data
public class FaWithdrawEntity {
    @TableId(type = IdType.AUTO)
    private Integer	id;

    /**分校人id*/
    private Integer userId;
    @TableField(exist = false)
    private String userName;
    /**金额*/
    private Double money;
    /**1微信2支付宝*/
    private Integer type;
    /**拒绝原因*/
    private String msg;
    /**0待审核1已通过待打款2已打款3不通过*/
    private Integer status;
    /**创建时间*/
    private String createTime;
    /**后台操作时间*/
    private String updateTime;

    /**
     * 支付宝账号
     */
    @TableField(exist = false)
    private String	zhiAccount;

    /**
     * 支付宝名字
     */
    @TableField(exist = false)
    private String	zhiName;
}
