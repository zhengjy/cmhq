package com.cmhq.core.app.model.rsp;

import lombok.Data;

@Data
public class HomeOrderRsp {
    /***/
    private String orderid;
    /**所属公司*/
    private String companyName;
    /**下单时间*/
    private String createTime;
    /**预计分佣*/
    private String yugufenyong;
    /***/
    private String cancel;
}
