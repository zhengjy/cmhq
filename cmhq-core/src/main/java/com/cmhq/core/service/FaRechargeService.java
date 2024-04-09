package com.cmhq.core.service;

import com.cmhq.core.model.FaCompanyEntity;
import com.cmhq.core.model.param.FaRechargeQuery;
import me.zhengjie.QueryResult;

/**
 * Created by Jiyang.Zheng on 2024/4/9 19:46.
 */
public interface FaRechargeService {
    QueryResult list(FaRechargeQuery query);

    Integer edit(FaCompanyEntity entity);

    void delete(Integer id);

    /**
     * 支付成功处理
     * @param outTradeNo
     * @param tradeNo
     */
    void applySuccessHandle(String outTradeNo,String tradeNo);
}
