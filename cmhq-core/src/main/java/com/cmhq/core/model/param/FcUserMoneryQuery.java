package com.cmhq.core.model.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by Jiyang.Zheng on 2024/4/9 13:20.
 */
@Data
public class FcUserMoneryQuery {
    @ApiModelProperty(value = "用户")
    private String username;
    @ApiModelProperty(value = "运单号")
    private String billCode;
    @ApiModelProperty(value = "公司")
    private String companyName;




    private Integer pageNo=1;

    private Integer pageSize=10;
}
