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
    MSG_7("退单订单扣除预估费用"),
    MSG_8("24未取件取消订单退回预估费用"),
    MSG_9("运费险赔付客户保额内退换货运费"),
    MSG_10("快递超长超重附加费"),
    MSG_11("快递保价费"),
    MSG_12("快递耗材（包装费）"),
    ;
    private String desc;

    MoneyConsumeMsgEumn(String desc) {
        this.desc = desc;
    }


    public String getDesc() {
        return desc;
    }

}
