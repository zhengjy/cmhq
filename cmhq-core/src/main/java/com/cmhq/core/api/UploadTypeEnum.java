package com.cmhq.core.api;


import com.cmhq.core.enums.CourierCompanyEnum;

public enum UploadTypeEnum {
    /**
     * 申通订单上传
     */
    TYPE_STO_ORDER_UPLOAD("sto_courier_order_upload","courier_order", CourierCompanyEnum.COMPANY_STO.getType(),"申通订单上传"),
    TYPE_JT_ORDER_UPLOAD("jt_courier_order_upload","courier_order", CourierCompanyEnum.COMPANY_JT.getType(),"极兔订单上传"),
    TYPE_JD_ORDER_UPLOAD("jd_courier_order_upload","courier_order", CourierCompanyEnum.COMPANY_JD.getType(),"订单上传"),


    TYPE_STO_QUERY_FREIGHT_CHARGE("sto_query_freight_charge","freight_charge",CourierCompanyEnum.COMPANY_STO.getType(),"查询申通时效运费"),
    TYPE_JT_QUERY_FREIGHT_CHARGE("jt_query_freight_charge","freight_charge",CourierCompanyEnum.COMPANY_JT.getType(),"查询极兔时效运费"),
    TYPE_JD_QUERY_FREIGHT_CHARGE("jd_query_freight_charge","freight_charge",CourierCompanyEnum.COMPANY_JD.getType(),"查询时效运费"),


    TYPE_STO_COURIER_QUERY_TRACK("sto_courier_query_track","courier_query_track",CourierCompanyEnum.COMPANY_STO.getType(),"物流轨迹查询sto"),
    TYPE_JT_COURIER_QUERY_TRACK("jt_courier_query_track","courier_query_track",CourierCompanyEnum.COMPANY_JT.getType(),"物流轨迹查询jt"),
    TYPE_JD_COURIER_QUERY_TRACK("jd_courier_query_track","courier_query_track",CourierCompanyEnum.COMPANY_JD.getType(),"物流轨迹查询"),

    TYPE_STO_CANCEL_COURIER_ORDER("sto_cancel_courier_order","cancel_courier_order",CourierCompanyEnum.COMPANY_STO.getType(),"取消订单sto"),
    TYPE_JT_CANCEL_COURIER_ORDER("jt_cancel_courier_order","cancel_courier_order",CourierCompanyEnum.COMPANY_JT.getType(),"取消订单jt"),
    TYPE_JD_CANCEL_COURIER_ORDER("jd_cancel_courier_order","cancel_courier_order",CourierCompanyEnum.COMPANY_JD.getType(),"取消订单"),

    TYPE_JD_QUERY_COURIER_ORDER_STATUS("query_courier_order_status","order_status",CourierCompanyEnum.COMPANY_JD.getType(),"查询订单状态"),
    TYPE_JD_QUERY_COURIER_ORDER_INFO("query_courier_order_info","order_info",CourierCompanyEnum.COMPANY_JD.getType(),"查询订单信息"),

    TYPE_BAIDU_ADDRDESS("baidu_addrdess","baidu_addrdess",CourierCompanyEnum.COMPANY_BAIDU.getType(),"百度地址识别"),
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
            if (value.getCourierCompanyCode().equals(ccc) && (value.getCode().contains(code)) ) {
                return value;
            }
        }
        return null;
    }

}
