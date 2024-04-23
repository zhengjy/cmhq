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
    /**orderIsErrorMsg:订单错误原因
     * goodsType:sto物品类型
     * jtGoodsType:jt物品类型
     * */
    private String cname;
    private String cvalue;
}
