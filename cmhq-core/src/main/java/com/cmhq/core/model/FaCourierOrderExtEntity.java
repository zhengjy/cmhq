package com.cmhq.core.model;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * Created by Jiyang.Zheng on 2024/4/8 14:11.
 */
@Data
@TableName("fa_courier_order_ext")
public class FaCourierOrderExtEntity {
    /**取消内容*/
    public static final String CNAME_CANCLE_CONTENT = "cancle_content";
    /**取消时间*/
    public static final String CNAME_CANCLE_TIME = "cancle_time";

    public FaCourierOrderExtEntity(){}

    public FaCourierOrderExtEntity(Integer courierOrderId, String cname, String cvalue) {
        this.courierOrderId = courierOrderId;
        this.cname = cname;
        this.cvalue = cvalue;
    }

    @TableId
    private Integer courierOrderId;
    /**orderIsErrorMsg:订单异常原因
     * goodsType:sto物品类型
     * jtGoodsType:jt物品类型
     * timeout24HourCourierCompanyCodes:超时24小时已下单的快递公司编号列表
     * order_fee_money:订单费用金额
     * order_fee_type:订单费用类型：超长超重额外费用
     * */
    private String cname;
    private String cvalue;
}
