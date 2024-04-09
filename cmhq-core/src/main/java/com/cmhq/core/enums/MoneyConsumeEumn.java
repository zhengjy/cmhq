package com.cmhq.core.enums;

/**
 * Created by Jiyang.Zheng on 2024/4/9 20:25.
 */
public enum MoneyConsumeEumn {
    CONSUM_1("1","系统"),
    CONSUM_2("2","充值"),
    CONSUM_3("3","消费"),
    ;
    private String type;
    private String desc;

    MoneyConsumeEumn(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public String getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }

    public static String getMsgByCode(String code) {
        for (MoneyConsumeEumn value : MoneyConsumeEumn.values()) {
            if (value.getType().equals(code) ) {
                return value.getDesc();
            }
        }
        return null;
    }
}
