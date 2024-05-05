package com.cmhq.core.enums;

/**
 *1运输中2派件中3已签收4异常
 */
public enum CourierWuliuStateEnum {
    /**
     * 本系統物流狀態
     */
    STATE_1("1","运输中"),
    STATE_2("2","派件中"),
    STATE_3("3","已签收"),
    STATE_4("4","異常件"),

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
