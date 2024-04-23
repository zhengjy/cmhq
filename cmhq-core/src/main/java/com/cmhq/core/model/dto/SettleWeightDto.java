package com.cmhq.core.model.dto;

import lombok.Data;

/**
 * Created by Jiyang.Zheng on 2024/4/23 9:17.
 */
@Data
public class SettleWeightDto {
    /**总运费*/
    private String totalFreight;
    /**计费重量*/
    private String packageChargeWeight;
    /**运单号*/
    private String waybillNo;
}
