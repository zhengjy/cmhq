package com.cmhq.core.enums;

/**
 *  订单状态 0待取件1已调派2已取件3:已取消
 */
public enum CourierOrderStateEnum {
    /**
     * 订单上传
     */
    STATE_0("0","待取件"),
    STATE_1("1","已调派"),
    STATE_2("2","已取件"),
    STATE_3("3","已取消"),
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
