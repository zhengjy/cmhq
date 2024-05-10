package com.cmhq.core.model.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by Jiyang.Zheng on 2024/4/5 9:44.
 */
@Data
@ApiModel("商户")
public class CompanyQuery {
    @ApiModelProperty(value = "公司名字")
    private String companyName;
    @ApiModelProperty(value = "手机号")
    private String mobile;
    @ApiModelProperty(value = "开始时间")
    private String sTime;
    @ApiModelProperty(value = "结束时间")
    private String eTime;

    private String notCid;
    private Integer id;

    private Integer pageNo=1;

    private Integer pageSize=10;
}
