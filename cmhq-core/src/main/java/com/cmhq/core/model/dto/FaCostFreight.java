package com.cmhq.core.model.dto;

import lombok.Data;

@Data
public class FaCostFreight {
    /**快递公司编码*/
    private String courierCompanyCode;
    /**首重（1kg）*/
    private Double price;
    /**续重（元/kg） */
    private Double priceTo;
}
