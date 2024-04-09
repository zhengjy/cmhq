package com.cmhq.core.model.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by Jiyang.Zheng on 2024/4/9 20:08.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class FcCompanyMoneyQuery  extends BaseQuery{
    @ApiModelProperty(value = "公司名称")
    private String companyName;
    @ApiModelProperty(value = "运单号")
    private String billNo;
    @ApiModelProperty(value = "类型：1系统2充值3消费")
    private String type;
}
