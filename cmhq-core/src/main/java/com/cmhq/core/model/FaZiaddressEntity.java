package com.cmhq.core.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by Jiyang.Zheng on 2024/4/9 12:36.
 */
@TableName( "fa_ziaddress")
@Data
public class FaZiaddressEntity {
    @TableId(type = IdType.AUTO)
    private Integer id;
    @ApiModelProperty(value = "地址")
    private String address;
    @ApiModelProperty(value = "手机号")
    private String mobile;
    @ApiModelProperty(value = "分机号")
    private String extel;
    @ApiModelProperty(value = "子账号id")
    private Integer cid;
    @ApiModelProperty(value = "父id")
    private Integer fid;
    @ApiModelProperty(value = "名字")
    private String name;
    @ApiModelProperty(value = "")
    private String provinceId;
    @ApiModelProperty(value = "")
    private String cityId;
    @ApiModelProperty(value = "")
    private String areaId;
    @ApiModelProperty(value = "")
    private String province;
    @ApiModelProperty(value = "")
    private String city;
    @ApiModelProperty(value = "")
    private String area;

}
