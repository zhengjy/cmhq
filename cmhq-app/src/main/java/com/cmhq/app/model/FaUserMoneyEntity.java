package com.cmhq.app.model;

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
    private Integer user_id;
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
    private Double after_money;
    /**创建时间*/
    private String create_time;
    /**商户id*/
    private Integer cid;
    /**是否分销 0正常1分销*/
    private String is_fenxiao="1";
}
