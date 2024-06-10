package com.cmhq.core.service;

import com.cmhq.core.model.param.CompanyMoneyParam;
import com.cmhq.core.model.param.FcCompanyMoneyQuery;
import me.zhengjie.QueryResult;

/**
 * Created by Jiyang.Zheng on 2024/4/9 19:47.
 */
public interface FaCompanyMoneyService {
    QueryResult list(FcCompanyMoneyQuery query);

    void delete(Integer id);

    /**
     * 插入消费记录 &返还商户或扣除商户金额
     */
    void saveRecord(CompanyMoneyParam param);
}
