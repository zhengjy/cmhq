package com.cmhq.core.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * Created by Jiyang.Zheng on 2024/4/7 14:46.
 */
@Data
@TableName("fa_courier_order")
public class FaCourierCompanyEntity {

    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "id")
    private Integer id;

    @ApiModelProperty(value = "商户id")
    private String courierName;

    @ApiModelProperty(value = "快递公司编码。如sto")
    private String courierCode;
    @ApiModelProperty(value = "认证信息")
    private String tokenInfo;
}
