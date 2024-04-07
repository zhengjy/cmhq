package com.cmhq.core.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import io.swagger.annotations.ApiModelProperty;
import javax.persistence.*;
import javax.validation.constraints.*;
import java.sql.Date;
import java.math.BigDecimal;

/**
 * Created by Jiyang.Zheng on 2024/4/6 13:46.
 */
@Data
@TableName("fa_courier_order")
public class FaCourierOrderEntity {

    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "id")
    private Integer id;

    @Column(name = "`fa_company_id`")
    @ApiModelProperty(value = "商户id")
    private Integer faCompanyId;

    @ApiModelProperty(value = "快递公司编码。如sto")
    private String courierCompanyCode;

    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @Column(name = "`courier_order_no`")
    @ApiModelProperty(value = "快递公司订单号")
    private String courierOrderNo;

    @Column(name = "`courier_company_waybill_no`")
    @ApiModelProperty(value = "快递公司运单号")
    private String courierCompanyWaybillNo;

    @Column(name = "`goods_type`")
    @ApiModelProperty(value = "物品分类")
    private String goodsType;

    @Column(name = "`goods_name`")
    @ApiModelProperty(value = "物品名称")
    private String goodsName;

    @Column(name = "`weight`")
    @ApiModelProperty(value = "重量")
    private Double weight;

    @Column(name = "`width`")
    @ApiModelProperty(value = "宽度")
    private Integer width;

    @Column(name = "`length`")
    @ApiModelProperty(value = "长度")
    private Integer length;

    @Column(name = "`height`")
    @ApiModelProperty(value = "高")
    private Integer height;

    @Column(name = "`estimate_price`")
    @ApiModelProperty(value = "商品金额")
    private BigDecimal estimatePrice;

    @Column(name = "`from_name`")
    @ApiModelProperty(value = "发货人")
    private String fromName;

    @Column(name = "`from_mobile`")
    @ApiModelProperty(value = "发货手机号")
    private String fromMobile;

    @Column(name = "`from_prov`")
    @ApiModelProperty(value = "发出省份")
    private String fromProv;

    @Column(name = "`from_city`")
    @ApiModelProperty(value = "发出城市")
    private String fromCity;

    @Column(name = "`from_area`")
    @ApiModelProperty(value = "发出区县")
    private String fromArea;

    @Column(name = "`from_address`")
    @ApiModelProperty(value = "发出地址")
    private String fromAddress;

    @Column(name = "`to_name`")
    @ApiModelProperty(value = "收货人")
    private String toName;

    @Column(name = "`to_mobile`")
    @ApiModelProperty(value = "收货手机号")
    private String toMobile;

    @Column(name = "`to_prov`")
    @ApiModelProperty(value = "收货省份")
    private String toProv;

    @Column(name = "`to_city`")
    @ApiModelProperty(value = "收货城市")
    private String toCity;

    @Column(name = "`to_area`")
    @ApiModelProperty(value = "收货区县")
    private String toArea;

    @Column(name = "`to_address`",nullable = false)
    @NotBlank
    @ApiModelProperty(value = "收货地址")
    private String toAddress;

    @Column(name = "`is_jiesuan`")
    @ApiModelProperty(value = "0未结算1已结算")
    private Integer isJiesuan;

    @ApiModelProperty(value = "订单状态：1运输中2派件中3已签收")
    private Integer orderState;
    @ApiModelProperty(value = "-1:待审核,0取消待审核1正常2审核通过3审核不通过")
    private Integer cancelOrderState;

    @ApiModelProperty(value = "0异常1正常")
    private Integer orderIsError;

    @ApiModelProperty(value = "用户id")
    private Integer createUserId;

    @Column(name = "`create_user`")
    @ApiModelProperty(value = "创建人")
    private String createUser;

    @Column(name = "`create_time`")
    @ApiModelProperty(value = "createTime")
    private Date createTime;

    @Column(name = "`update_user`")
    @ApiModelProperty(value = "更新人")
    private String updateUser;

    @Column(name = "`update_time`")
    @ApiModelProperty(value = "updateTime")
    private Date updateTime;

    @ApiModelProperty(value = "扩展信息")
    private String courierOrderExtend;
}
