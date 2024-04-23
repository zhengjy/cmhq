package com.cmhq.core.enums;

/**
 * Created by Jiyang.Zheng on 2024/4/9 20:25.
 */
public enum MoneyConsumeMsgEumn {
    MSG_1("充值余额增加"),
    MSG_2("货物签收扣除实际费用"),
    MSG_3("创建订单扣除预估费用"),
    MSG_4("货物签收退回预估费用"),
    MSG_5("快递员取消订单退回预估费用"),
    MSG_6("取消订单退回预估费用"),
    MSG_7("创建订单预估重量和实际重量不符增加扣除预估费用"),
    ;
    private String desc;

    MoneyConsumeMsgEumn(String desc) {
        this.desc = desc;
    }


    public String getDesc() {
        return desc;
    }

}
