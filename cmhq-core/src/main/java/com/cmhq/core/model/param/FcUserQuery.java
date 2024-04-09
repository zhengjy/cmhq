package com.cmhq.core.model.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by Jiyang.Zheng on 2024/4/9 13:20.
 */
@Data
public class FcUserQuery {
    @ApiModelProperty(value = "姓名")
    private String username;
    @ApiModelProperty(value = "用户名")
    private String nickname;
    @ApiModelProperty(value = "开始时间")
    private String sTime;
    @ApiModelProperty(value = "结束时间")
    private String eTime;

    @ApiModelProperty(value = "状态")
    private String status;



    private Integer pageNo=1;

    private Integer pageSize=10;
}
