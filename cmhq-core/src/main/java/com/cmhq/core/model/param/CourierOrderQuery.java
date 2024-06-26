package com.cmhq.core.model.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * Created by Jiyang.Zheng on 2024/4/6 14:05.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("物流订单")
public class CourierOrderQuery extends BaseQuery{
    @ApiModelProperty(value = "快递公司 sto：申通")
    private String courierCompanyCode;
    @ApiModelProperty(value = "id")
    private Integer id;
    @ApiModelProperty(value = "订单号")
    private String orderNo;
    @ApiModelProperty(value = "运单号")
    private String courierCompanyWaybillNo;
    @ApiModelProperty(value = "物品名称")
    private String goodsName;
    @ApiModelProperty(value = "页面菜单：1：商户-订单列表，2：商户-订单取消记录，3：商户-取消订单审核，4：总后台-发货订单，5：总后台-订单取消记录，6：总后台-取消订单审核")
    private String pagetype;

    @ApiModelProperty(value = "本系统自定义状态:0待取件1调派2已取件3:已取消")
    private List<Integer> orderState;

    @ApiModelProperty(value = "本系统自定义物流息状态：1运输中2派件中3已签收4异常")
    private List<Integer> wuliuState;

    @ApiModelProperty(value = "-1:待审核,0取消待审核1正常2审核通过3审核不通过")
    private List<Integer> cancelOrderState;

    @ApiModelProperty(value = "是否异常：0异常1正常")
    private String orderIsError;

    @ApiModelProperty(value = "是否结算：0未结算1已结算")
    private String isJiesuan;
    @ApiModelProperty(value = "是否取消：1是")
    private String isCancel;

    @ApiModelProperty(value = "发货人")
    private String fromName;

    @ApiModelProperty(value = "发货手机号")
    private String fromMobile;

    @ApiModelProperty(value = "收货人")
    private String toName;

    @ApiModelProperty(value = "收货手机号")
    private String toMobile;

    @ApiModelProperty(value = "商户公司")
    private String companyName;
    @ApiModelProperty(value = "帐户名称")
    private String userName;

    @ApiModelProperty(value = "开始时间")
    private String sTime;
    @ApiModelProperty(value = "结束时间")
    private String eTime;

}
