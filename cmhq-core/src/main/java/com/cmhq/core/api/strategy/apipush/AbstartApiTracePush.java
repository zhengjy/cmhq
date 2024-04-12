package com.cmhq.core.api.strategy.apipush;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cmhq.core.api.UploadData;
import com.cmhq.core.enums.CourierOrderStateEnum;
import com.cmhq.core.enums.CourierWuliuStateEnum;
import com.cmhq.core.enums.MoneyConsumeEumn;
import com.cmhq.core.enums.MoneyConsumeMsgEumn;
import com.cmhq.core.model.CompanyMoneyParam;
import com.cmhq.core.model.FaCourierOrderEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;

/**
 * 物流状态处理
 * Created by Jiyang.Zheng on 2024/4/11 21:03.
 */
@Slf4j
@Component
public abstract class AbstartApiTracePush< Req extends UploadData> extends AbstractApiPush<Req> {

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
        //获取状态
        CourierWuliuStateEnum wuliuStateEnum = getTraceState(req);
        //获取状态
        FaCourierOrderEntity orderEntity = new FaCourierOrderEntity();
        orderEntity.setWuliuState(Integer.parseInt(wuliuStateEnum.getType()));
        if (wuliuStateEnum.getType().equals(CourierWuliuStateEnum.STATE_3.getType())){
            //
            orderEntity.setIsJiesuan(1);
            orderEntity.setOrderIsError(1);
            faCourierOrderDao.update(orderEntity, new LambdaQueryWrapper<FaCourierOrderEntity>().eq(FaCourierOrderEntity::getCourierCompanyWaybillNo,req.getUnKey()));
            //订单签收执行
            faCompanyMoneyService.saveRecord(new CompanyMoneyParam(2, MoneyConsumeEumn.CONSUM_3, MoneyConsumeMsgEumn.MSG_2, order.getEstimatePrice(),order.getFaCompanyId(),order.getId()+""));
        }
    }

    /**
     *
     * @param req
     * @return
     */
    protected abstract CourierWuliuStateEnum getTraceState(Req req);

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
