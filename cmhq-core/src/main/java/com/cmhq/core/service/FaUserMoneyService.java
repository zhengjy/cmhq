package com.cmhq.core.service;

import com.cmhq.core.model.param.CompanyMoneyParam;
import com.cmhq.core.model.param.UserMoneyParam;

public interface FaUserMoneyService {
    /**
     * 插入消费记录 &返还商户或扣除商户金额
     */
    void saveRecord(UserMoneyParam param);
}
