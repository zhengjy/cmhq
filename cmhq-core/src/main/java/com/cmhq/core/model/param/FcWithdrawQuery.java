package com.cmhq.core.model.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by Jiyang.Zheng on 2024/4/9 13:20.
 */
@Data
public class FcWithdrawQuery {
    @ApiModelProperty(value = "用户手机号")
    private String username;
    @ApiModelProperty(value = "运单号")
    private Integer status;




    private Integer pageNo=1;

    private Integer pageSize=10;
}
