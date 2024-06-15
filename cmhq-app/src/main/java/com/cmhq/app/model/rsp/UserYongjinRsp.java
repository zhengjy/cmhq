package com.cmhq.app.model.rsp;

import lombok.Data;

import java.util.List;

@Data
public class UserYongjinRsp {
    /**可提现金额(元)*/
    private Double ketixian;
    /**预估分佣金额(元)*/
    private Double yugufenyong;
    /**已提现金额(元)*/
    private Double yitixian;

    private List list;
}
