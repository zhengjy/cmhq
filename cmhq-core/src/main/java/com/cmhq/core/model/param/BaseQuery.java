package com.cmhq.core.model.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by Jiyang.Zheng on 2024/4/9 20:06.
 */
@Data
public class BaseQuery {
    @ApiModelProperty(value = "开始时间")
    private String sTime;
    @ApiModelProperty(value = "结束时间")
    private String eTime;

    @ApiModelProperty(value = "状态")
    private String status;



    private Integer pageNo=1;

    private Integer pageSize=10;
}
