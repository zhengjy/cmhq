package com.cmhq.core.api;


import com.cmhq.core.enums.CourierCompanyEnum;

public enum UploadTypeEnum {
    /**
     * 申通订单上传
     */
    TYPE_STO_ORDER_UPLOAD("sto_courier_order_upload","courier_order", CourierCompanyEnum.COMPANY_STO.getType(),"申通订单上传"),
    TYPE_JT_ORDER_UPLOAD("jt_courier_order_upload","courier_order", CourierCompanyEnum.COMPANY_JT.getType(),"极兔订单上传"),


    TYPE_STO_QUERY_FREIGHT_CHARGE("sto_query_freight_charge","freight_charge",CourierCompanyEnum.COMPANY_STO.getType(),"查询申通时效运费"),
    TYPE_JT_QUERY_FREIGHT_CHARGE("jt_query_freight_charge","freight_charge",CourierCompanyEnum.COMPANY_JT.getType(),"查询极兔时效运费"),


    TYPE_STO_CANCEL_COURIER_ORDER("sto_cancel_courier_order","freight_charge",CourierCompanyEnum.COMPANY_STO.getType(),"取消订单sto"),
    TYPE_JT_CANCEL_COURIER_ORDER("jt_cancel_courier_order","cancel_courier_order",CourierCompanyEnum.COMPANY_JT.getType(),"取消订单jt"),
    ;
    private String code;
    private String codeNickName;
    private String courierCompanyCode;
    private String desc;

    UploadTypeEnum(String type,String codeNickName,String courierCompanyCode, String desc) {
        this.code = type;
        this.codeNickName = codeNickName;
        this.courierCompanyCode = courierCompanyCode;
        this.desc = desc;
    }

    public String getCodeNickName() {
        return codeNickName;
    }
    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
    public String getCourierCompanyCode() {
        return courierCompanyCode;
    }

    public static UploadTypeEnum getMsgByCode(String ccc,String code) {
        for (UploadTypeEnum value : UploadTypeEnum.values()) {
            if (value.getCourierCompanyCode().equals(ccc) && (value.getCode().contains(code) && value.getCode().contains(ccc)) ) {
                return value;
            }
        }
        return null;
    }

}
