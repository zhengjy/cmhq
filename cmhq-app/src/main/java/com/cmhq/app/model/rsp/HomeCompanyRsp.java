package com.cmhq.app.model.rsp;


import lombok.Data;

@Data
public class HomeCompanyRsp {
    /***/
    private String company_name ;
    /**订单总数*/
    private Integer all_orders;
    /**累计分佣金额*/
    private Double all_fenyong;
    /**预计分佣金额*/
    private Double yugufenyong;
}
