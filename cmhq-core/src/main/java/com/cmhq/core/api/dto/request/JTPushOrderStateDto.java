package com.cmhq.core.api.dto.request;

import com.cmhq.core.api.UploadData;
import lombok.Data;

/**
 * https://open.jtexpress.com.cn/#/apiDoc/orderserve/statusFeedback
 * 订单状态回传
 * Created by Jiyang.Zheng on 2024/4/21 14:07.
 */
@Data
public class JTPushOrderStateDto extends UploadData {
    /**客户订单编号*/
    private String txlogisticId;
    /**极兔运单编号*/
    private String billCode;
    /**极兔订单编号*/
    private String jtOrderId;
    /**重量（“已揽收”“已取件”时回传）*/
    private String weight;
    /**订单取消原因（“已取消”时回传）*/
    private String reason;
    /**状态(“已调派业务员”,“已揽收”，“已取件”，“已取消”)*/
    private String scanType;
    /**时间*/
    private String time;
    /**承运商编码 */
    private String carrierCode;

    @Override
    public String getUnKeyValue() {
        return txlogisticId;
    }
}
