package com.cmhq.core.enums;

/**
 *  订单状态 1未调派业务员2已调派业务员3已揽收4已取件5已取消
 */
public enum CourierWuliuStateEnum {
    /**
     * 申通订单上传
     */
    STATE_1("1","运输中"),
    STATE_2("2","派件中"),
    STATE_3("3","已签收"),
    ;
    private String type;
    private String desc;

    CourierWuliuStateEnum(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public String getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }

}
