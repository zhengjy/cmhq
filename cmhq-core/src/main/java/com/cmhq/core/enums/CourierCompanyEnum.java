package com.cmhq.core.enums;


public enum CourierCompanyEnum {
    /**
     * 申通订单上传
     */
    COMPANY_STO("sto","申通"),
    COMPANY_JT("J&T","极兔"),
    ;
    private String type;
    private String desc;

    CourierCompanyEnum(String type,  String desc) {
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
