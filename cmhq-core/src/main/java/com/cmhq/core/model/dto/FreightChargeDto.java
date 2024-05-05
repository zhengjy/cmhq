package com.cmhq.core.model.dto;

import lombok.Data;

/**
 * Created by Jiyang.Zheng on 2024/4/20 10:18.
 */
@Data
public class FreightChargeDto {


    /**预估运费（单位元）*/
    private Double totalPrice;
    /**快递公司编码*/
    private String courierCompanyCode;

    private String errorMsg;
}
