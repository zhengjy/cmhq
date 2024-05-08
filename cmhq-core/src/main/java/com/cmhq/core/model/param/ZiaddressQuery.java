package com.cmhq.core.model.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by Jiyang.Zheng on 2024/4/5 9:44.
 */
@Data
@ApiModel("地址庫")
public class ZiaddressQuery {
    @ApiModelProperty(value = "搜索內容")
    private String searchTxt;


    private Integer companyId;
    private Integer pageNo;

    private Integer pageSize;
}
