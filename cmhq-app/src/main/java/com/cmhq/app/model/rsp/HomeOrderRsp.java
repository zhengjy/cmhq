package com.cmhq.app.model.rsp;

import lombok.Data;

@Data
public class HomeOrderRsp {
    /***/
    private String orderid;
    /**所属公司*/
    private String company_name;
    /**下单时间*/
    private String create_time;
    /**预计分佣*/
    private String yugufenyong;
    /***/
    private String cancel;
}
