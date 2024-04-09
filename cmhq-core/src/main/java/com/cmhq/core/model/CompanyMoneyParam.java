package com.cmhq.core.model;

import com.cmhq.core.enums.MoneyConsumeEumn;
import com.cmhq.core.enums.MoneyConsumeMsgEumn;
import lombok.Data;

/**
 * Created by Jiyang.Zheng on 2024/4/9 21:04.
 */
@Data
public class CompanyMoneyParam {

    /**1增加2减少*/
    private Integer addType;
    MoneyConsumeEumn consumeEumn;
    MoneyConsumeMsgEumn msgEumn;
    double money;
    /**公司id*/
    Integer companyId;
    /**订单号*/
    private String orderId;
    /**运单号*/
    private String billCode;

    public CompanyMoneyParam(Integer addType, MoneyConsumeEumn consumeEumn, MoneyConsumeMsgEumn msgEumn, double money, Integer companyId, String orderId) {
        this.addType = addType;
        this.consumeEumn = consumeEumn;
        this.msgEumn = msgEumn;
        this.money = money;
        this.companyId = companyId;
        this.orderId = orderId;
    }

    public CompanyMoneyParam(Integer addType, MoneyConsumeEumn consumeEumn, MoneyConsumeMsgEumn msgEumn, double money, Integer companyId, String orderId, String billCode) {
        this.addType = addType;
        this.consumeEumn = consumeEumn;
        this.msgEumn = msgEumn;
        this.money = money;
        this.companyId = companyId;
        this.orderId = orderId;
        this.billCode = billCode;
    }
}
