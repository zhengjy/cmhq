package com.cmhq.core.app.model.rsp;


import lombok.Data;

@Data
public class HomeCompanyRsp {
    /***/
    private String companyName ;
    /**订单总数*/
    private Integer allOrders;
    /**累计分佣金额*/
    private Double allFenyong;
    /**预计分佣金额*/
    private Double yugufenyong;
}
