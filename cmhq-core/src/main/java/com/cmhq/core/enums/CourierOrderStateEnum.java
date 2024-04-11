package com.cmhq.core.enums;

/**
 *  订单状态 1未调派业务员2已调派业务员3已揽收4已取件5已取消
 */
public enum CourierOrderStateEnum {
    /**
     * 申通订单上传
     */
    STATE_1("1","未调派业务员"),
    STATE_2("2","已调派业务员"),
    STATE_3("3","已揽收"),
    STATE_4("4","已取件"),
    STATE_5("5","已取消"),
    ;
    private String type;
    private String desc;

    CourierOrderStateEnum(String type, String desc) {
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
