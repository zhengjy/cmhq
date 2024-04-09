package com.cmhq.core.model.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Created by Jiyang.Zheng on 2024/4/9 20:06.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class FaRechargeQuery extends BaseQuery{
    @ApiModelProperty(value = "订单号")
    private String orderId;
    @ApiModelProperty(value = "0未支付1已支付")
    private String status;
    @ApiModelProperty(value = "公司名称")
    private String companyName;
    @ApiModelProperty(value = "支付开始时间")
    private String playSTime;
    @ApiModelProperty(value = "支付结束时间")
    private String playETime;

}
