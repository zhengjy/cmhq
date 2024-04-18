package com.cmhq.core.model.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by Jiyang.Zheng on 2024/4/18 15:43.
 */
@Data
@ApiModel("商户首页")
public class CompanyHomePage {
    @ApiModelProperty(value = "公司账户总额")
    private Double money;

    @ApiModelProperty(value = "本月已取件成功")
    private Integer toMonthQjSuccess;

    @ApiModelProperty(value = "本月已签收")
    private Integer toMonthQsSuccess;

    @ApiModelProperty(value = "近三个月已取件成功")
    private Integer qj3success;

    @ApiModelProperty(value = "最近三个月已签收")
    private Integer qs3sussess;

    @ApiModelProperty(value = "最近三个月已取消订单")
    private Integer cancel3;

    @ApiModelProperty(value = "今日已下单")
    private Integer toDayOrder;

    @ApiModelProperty(value = "待取件")
    private Integer dqj;

    @ApiModelProperty(value = "今日已取件成功")
    private Integer todayQjSuccess;

    @ApiModelProperty(value = "运输中")
    private Integer transitIn;

    @ApiModelProperty(value = "今日派件中")
    private Integer todayPsIn;

    @ApiModelProperty(value = "今日已签收")
    private Integer todayQs;

    @ApiModelProperty(value = "超48小时未揽件成功的订单(需反馈)")
    private Integer wlj48;

    @ApiModelProperty(value = "已调派业务员状态下取消订单（下单人取消）")
    private Integer ydpCancel;

    @ApiModelProperty(value = "待取件异常订单消息")
    private Integer dqjException;

    @ApiModelProperty(value = "未调派业务员状态下取消订单（下单人取消）")
    private Integer wdpCancel;

    @ApiModelProperty(value = "已调派业务员状态下主动取消订单（快递员取消通知）")
    private Integer ydpCancel2;

    @ApiModelProperty(value = "已取件异常订单消息")
    private Integer yqjException;


}
