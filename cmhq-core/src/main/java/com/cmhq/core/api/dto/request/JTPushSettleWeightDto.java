package com.cmhq.core.api.dto.request;

import com.cmhq.core.api.UploadData;
import lombok.Data;

/**
 * 结算账单回传
 * https://open.jtexpress.com.cn/#/apiDoc/other/settlementReturn
 * Created by Jiyang.Zheng on 2024/4/17 17:28.
 */
@Data
public class JTPushSettleWeightDto extends UploadData {
    /**运单号*/
    private String waybillNo;
    /**总运费*/
    private String totalFreight;
    /**计费重量*/
    private String packageChargeWeight;
    /**保价费*/
    private String insuredFee;
    /**运费*/
    private String freight;
    /**客户编号*/
    private String customerCode;

    @Override
    public String getUnKeyValue() {
        return waybillNo;
    }
}
