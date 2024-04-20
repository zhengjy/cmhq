package com.cmhq.core.model.dto;

import lombok.Data;

/**
 * Created by Jiyang.Zheng on 2024/4/20 10:18.
 */
@Data
public class FreightChargeDto {

    /**首重价格（单位分）*/
    private int startPrice;
    /**续重重量（单位克），重量调整粒度*/
    private int continuedHeavy;
    /**首重重量（单位克）*/
    private int startWeight;
    /**续重费用（单位分），每增加一份续重重量需要增加的价格*/
    private int continuedHeavyPrice;
    /**预估运费（单位元）*/
    private Double totalPrice;
    /**快递公司编码*/
    private String courierCompanyCode;
}
