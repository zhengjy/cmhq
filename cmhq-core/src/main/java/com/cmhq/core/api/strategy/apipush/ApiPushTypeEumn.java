package com.cmhq.core.api.strategy.apipush;

/**
 * Created by Jiyang.Zheng on 2024/4/9 20:25.
 */
public enum ApiPushTypeEumn {
    TYPE_STO_PUSHORDERSTATUS("sto_pushOrderStatus","sto_推送订单状态"),
    TYPE_STO_PUSHTRACE("sto_pushTrace","sto_物流轨迹推送"),
    TYPE_STO_PUSHSETTLEWEIGHT("sto_pushSettleWeight","sto_结算重量回传"),
    ;
    private String type;
    private String desc;

    ApiPushTypeEumn(String type, String desc) {
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
        for (ApiPushTypeEumn value : ApiPushTypeEumn.values()) {
            if (value.getType().equals(code) ) {
                return value.getDesc();
            }
        }
        return null;
    }
}
