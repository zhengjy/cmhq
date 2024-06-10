package com.cmhq.core.enums;

/**
 * Created by Jiyang.Zheng on 2024/4/9 20:25.
 */
public enum UserMoneyConsumeMsgEumn {
    MSG_1("货物签收获得分销提成"),
    MSG_2("提现扣除余额"),
    MSG_3("驳回提现退回余额"),
    ;
    private String desc;

    UserMoneyConsumeMsgEumn(String desc) {
        this.desc = desc;
    }


    public String getDesc() {
        return desc;
    }

}
