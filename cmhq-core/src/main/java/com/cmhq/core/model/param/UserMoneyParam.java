package com.cmhq.core.model.param;

import com.cmhq.core.enums.UserMoneyConsumeMsgEumn;
import lombok.Data;

@Data
public class UserMoneyParam {
    /**1增加2减少*/
    private Integer addType;
    UserMoneyConsumeMsgEumn msgEumn;
    double money;
    /**公司id*/
    Integer companyId;
    /**分销人id*/
    Integer userId;
    /**
     * 分销比例
     */
    private Integer	distributionRatio;
    /**订单号 ,本系统订单表id*/
    private String orderId;
    /**运单号*/
    private String billCode;

    public UserMoneyParam(Integer addType, UserMoneyConsumeMsgEumn msgEumn, double money, Integer companyId, Integer userId,Integer	distributionRatio, String orderId, String billCode) {
        this.addType = addType;
        this.msgEumn = msgEumn;
        this.money = money;
        this.companyId = companyId;
        this.userId = userId;
        this.distributionRatio = distributionRatio;
        this.orderId = orderId;
        this.billCode = billCode;
    }
}
