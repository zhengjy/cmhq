package com.cmhq.core.api.strategy.apipush;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cmhq.core.api.UploadData;
import com.cmhq.core.api.dto.request.StoPushOrderStateDto;
import com.cmhq.core.enums.CourierOrderStateEnum;
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
 * 快递订单状态处理
 * Created by Jiyang.Zheng on 2024/4/11 21:03.
 */
@Slf4j
@Component
public abstract class AbstartApiOrderPush<Req extends UploadData> extends AbstractApiPush<Req> {

    @Override
    protected void doPushHandle(Req req) {
        //获取状态
        CourierOrderStateEnum orderStateEnum = getOrderState(req);
        if (orderStateEnum == null){
            log.error("未匹配到订单状态 params 【{}】", JSONObject.toJSONString(req));
            return;
        }
        FaCourierOrderEntity orderEntity = new FaCourierOrderEntity();
        orderEntity.setOrderState(Integer.parseInt(orderStateEnum.getType()));
        orderEntity.setCourierOrderState(getCourierOrderState(req));
        if (orderStateEnum.getType().equals(CourierOrderStateEnum.STATE_3.getType())){
            //
            orderEntity.setCancelType(2);
            orderEntity.setReason(getCanceReason(req));
            faCourierOrderDao.update(orderEntity, new LambdaQueryWrapper<FaCourierOrderEntity>().eq(FaCourierOrderEntity::getOrderNo,req.getUnKey()));
            otherHandle(req);
            FaCourierOrderEntity order = getFaCourierOrder(req);
            if (order == null){
                log.error("为查询到订单 params 【{}】", JSONObject.toJSONString(order));
                return;
            }
            //取消订单。返还账户金额
            faCompanyMoneyService.saveRecord(new CompanyMoneyParam(1, MoneyConsumeEumn.CONSUM_1, MoneyConsumeMsgEumn.MSG_5, order.getEstimatePrice(),order.getFaCompanyId(),order.getId()+""));
        }else {
            faCourierOrderDao.update(orderEntity, new LambdaQueryWrapper<FaCourierOrderEntity>().eq(FaCourierOrderEntity::getOrderNo,req.getUnKey()));
        }

    }

    /**
     *
     * @param req
     * @return
     */
    protected abstract String getCanceReason(Req req);
    protected abstract String getCourierOrderState(Req req);

    protected abstract CourierOrderStateEnum getOrderState(Req req);

    /**
     * 其他業務處理
     * @param req
     */
    protected abstract void otherHandle(Req req);

    @Override
    protected FaCourierOrderEntity getFaCourierOrder(Req req) {
        List<FaCourierOrderEntity> list = faCourierOrderDao.selectList(new LambdaQueryWrapper<FaCourierOrderEntity>().eq(FaCourierOrderEntity::getOrderNo,req.getUnKey()));
        if (CollectionUtils.isNotEmpty(list)){
            return list.get(0);
        }
        return null;
    }


    @Override
    protected void afterHandle(Req req) {

    }
}
