package com.cmhq.core.model.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by Jiyang.Zheng on 2024/4/5 9:44.
 */
@Data
@ApiModel("物流报价")
public class CostQuery extends BaseQuery{
    @ApiModelProperty(value = "始发省")
    private String fromPro;
    @ApiModelProperty(value = "目的省份")
    private String toPro;
    @ApiModelProperty(value = "始发地")
    private String fromCity;
    @ApiModelProperty(value = "目的地")
    private String toCity;

    @ApiModelProperty(value = "首重(1kg)-开始")
    private Double  sPrice;
    @ApiModelProperty(value = "首重(1kg)-结束")
    private Double  ePrice;
    @ApiModelProperty(value = "续重(1kg)-开始")
    private Double  sToPrice;
    @ApiModelProperty(value = "续重(1kg)-结束")
    private Double  eToPrice;
}
