package com.cmhq.core.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

/**
 * Created by Jiyang.Zheng on 2024/4/6 13:46.
 */
@Data
@TableName("fa_expressorder")
public class FaCourierOrderEntity {

    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "id")
    private Integer id;

    @ApiModelProperty(value = "本系统自定义订单号")
    @TableField(value = "orderid")
    private String orderNo;

    @ApiModelProperty(value = "快递公司订单号")
    private String courierCompanyOrderNo;

    @ApiModelProperty(value = "快递公司编码。如sto")
    private String courierCompanyCode;

    @ApiModelProperty(value = "快递公司运单号")
    @TableField("billCode")
    private String courierCompanyWaybillNo;

    @ApiModelProperty(value = "商户id")
    @TableField("cid")
    private Integer faCompanyId;

    @ApiModelProperty(value = "子账户id")
    @TableField("zid")
    private Integer zid; //TODO

    @ApiModelProperty(value = "物品分类ID")
    @Deprecated
    private Integer goodsCategoryId;

//    @ApiModelProperty(value = "物品分类")
//    private String goodsType;

    @ApiModelProperty(value = "物品名称")
    private String goodsName;

    @ApiModelProperty(value = "预估重量（页面填写的）")
    private Double weight;

    @ApiModelProperty(value = "本地实际重量")
    private Double weightto;

    @ApiModelProperty(value = "宽度")
    private Integer width;

    @ApiModelProperty(value = "长度")
    private Integer length;

    @ApiModelProperty(value = "高")
    private Integer height;

    @ApiModelProperty(value = "预估费用")
    private Double estimatePrice;
    @ApiModelProperty(value = "实际费用")
    private Double price;
    @ApiModelProperty(value = "本地实际费用")
    private Double priceto;

    @ApiModelProperty(value = "期望上门取件时间")
    private String takeGoodsTime;

    @ApiModelProperty(value = "1上门取件2门店自寄")
    private Integer type =1;

    @ApiModelProperty(value = "本系统自定义状态:0待取件1已取件2已发出3已签收")
    @TableField("status")
    private Integer orderState;

    @ApiModelProperty(value = "-1:待审核,0取消待审核1正常2审核通过3审核不通过")
    @TableField("cancel")
    private Integer cancelOrderState=1;

    @ApiModelProperty(value = "发货人")
    private String fromName;

    @ApiModelProperty(value = "发货手机号")
    private String fromMobile;

    @ApiModelProperty(value = "发出省份")
    private String fromProv;

    @ApiModelProperty(value = "发出城市")
    private String fromCity;

    @ApiModelProperty(value = "发出区县")
    private String fromArea;

    @ApiModelProperty(value = "发出地址")
    private String fromAddress;

    @ApiModelProperty(value = "收货人")
    private String toName;

    @ApiModelProperty(value = "收货手机号")
    private String toMobile;

    @ApiModelProperty(value = "收货省份")
    private String toProv;

    @ApiModelProperty(value = "收货城市")
    private String toCity;

    @ApiModelProperty(value = "收货区县")
    private String toArea;

    @ApiModelProperty(value = "收货地址")
    private String toAddress;


    @ApiModelProperty(value = "物流公司物流信息状态：1运输中2派件中3已签收")
    @TableField("jitu_wuliu")
    private Integer wuliuState;

    @ApiModelProperty(value = "物流公司订单状态：1未调派业务员2已调派业务员3已揽收4已取件5已取消")
    @TableField("jitu_status")
    private Integer courierOrderState;

    /**
     * 异常件 in(取消订单、)
     */
    @ApiModelProperty(value = "0异常1正常")
    @TableField("is_error")
    private Integer orderIsError;

    @ApiModelProperty(value = "用户id")
    private Integer createUserId;

    @ApiModelProperty(value = "创建人")
    @TableField(exist = false)
    private String createUserName;

    @ApiModelProperty(value = "createTime")
    private Date createTime;
    @ApiModelProperty(value = "取消时间")
    private Date cancelTime;
    @ApiModelProperty(value = "取消原因")
    private String reason;

    @ApiModelProperty(value = "预计结算时间戳签收7天后")
    private Date jiesuanTime;
    @ApiModelProperty(value = "0未结算1已结算")
    private Integer isJiesuan=0;

    @ApiModelProperty(value = "1自己取消2物流公司取消")
    private Integer cancelType;

    @ApiModelProperty(value = "备注")
    private String msg;
    @ApiModelProperty(value = "取件时间")
    private String qjTime;
    @ApiModelProperty(value = "签收时间")
    private String qsTime;

    @ApiModelProperty(value = "扩展信息")
    @TableField(exist = false)
    private String courierOrderExtend;

    @TableField(exist = false)
    private String	companyName;

}
