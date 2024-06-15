package com.cmhq.app.model;

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
    private Integer user_id;
    /**金额*/
    private Double money;
    /**1微信2支付宝*/
    private Integer type;
    /**拒绝原因*/
    private String msg;
    /**0待审核1已通过待打款2已打款3不通过*/
    private Integer status;
    /**创建时间*/
    private String create_time;

    /**
     * 支付宝账号
     */
    private String	zhi_account;

    /**
     * 支付宝名字
     */
    private String	zhi_name;
}
