package com.cmhq.core.api.strategy.apipush;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cmhq.core.api.UploadData;
import com.cmhq.core.enums.CourierWuliuStateEnum;
import com.cmhq.core.enums.MoneyConsumeEumn;
import com.cmhq.core.enums.MoneyConsumeMsgEumn;
import com.cmhq.core.model.CompanyMoneyParam;
import com.cmhq.core.model.FaCourierOrderEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 结算重量回传
 * Created by Jiyang.Zheng on 2024/4/11 21:03.
 */
@Slf4j
@Component
public abstract class AbstartApiSettleWeightPush< Req extends UploadData> extends AbstractApiPush<Req> {

    @Override
    public void doPushHandle(Req req) {
        FaCourierOrderEntity order = getFaCourierOrder(req);
        if (order == null){
            log.error("为查询到订单 params 【{}】", JSONObject.toJSONString(order));
            return;
        }
        if (order.getWuliuState().equals(CourierWuliuStateEnum.STATE_3.getType())){
            log.error("订单已签收 params 【{}】", JSONObject.toJSONString(order));
            return;
        }
        getWeight(req);
        //

    }

    protected abstract Double getWeight(Req req);

    @Override
    protected FaCourierOrderEntity getFaCourierOrder(Req req) {
        List<FaCourierOrderEntity> list = faCourierOrderDao.selectList(new LambdaQueryWrapper<FaCourierOrderEntity>().eq(FaCourierOrderEntity::getCourierCompanyWaybillNo,req.getUnKey()));
        if (CollectionUtils.isNotEmpty(list)){
            return list.get(0);
        }
        return null;
    }

    @Override
    protected void afterHandle(Req req) {

    }
}
