package com.cmhq.core.api.dto.request;

import com.cmhq.core.api.UploadData;
import lombok.Data;

/**
 * 结算重量回传
 * https://open.sto.cn/#/apiDocument/MARKET_PUSH_SETTLE_WEIGHT
 * Created by Jiyang.Zheng on 2024/4/17 17:28.
 */
@Data
public class StoPushSettleWeightDto extends UploadData {
    /**运单号*/
    private String BillCode;
    /**结算重量*/
    private String SettleWeight;

    @Override
    public String getUnKeyValue() {
        return BillCode;
    }
}
