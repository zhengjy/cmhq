package com.cmhq.core.service;

import com.cmhq.core.model.CompanyMoneyParam;
import com.cmhq.core.model.param.FcCompanyMoneyQuery;
import me.zhengjie.QueryResult;

/**
 * Created by Jiyang.Zheng on 2024/4/9 19:47.
 */
public interface FaCompanyMoneyService {
    QueryResult list(FcCompanyMoneyQuery query);

    void delete(Integer id);

    /**
     * 存储余额记录
     */
    void saveRecord(CompanyMoneyParam param);
}
