package com.cmhq.core.api.strategy.apipush;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cmhq.core.api.UploadData;
import com.cmhq.core.enums.CourierOrderStateEnum;
import com.cmhq.core.model.FaCourierOrderEntity;
import org.springframework.stereotype.Component;

/**
 * 快递订单状态处理
 * Created by Jiyang.Zheng on 2024/4/11 21:03.
 */
@Component
public abstract class AbstartApiOrderPush<Req extends UploadData> extends AbstractApiPush<Req > {
    @Override
    protected void doPushHandle(Req req) {
        //获取状态
        CourierOrderStateEnum orderStateEnum = getOrderState(req);
        FaCourierOrderEntity orderEntity = new FaCourierOrderEntity();
        orderEntity.setOrderState(Integer.parseInt(orderStateEnum.getType()));
        if (orderStateEnum.getType().equals(CourierOrderStateEnum.STATE_5.getType())){
            //
            orderEntity.setCancelType(2);
            orderEntity.setReason(getCanceReason(req));
            faCourierOrderDao.update(orderEntity, new LambdaQueryWrapper<FaCourierOrderEntity>().eq(FaCourierOrderEntity::getCourierCompanyOrderNo,req.getUnKey()));
            //取消返还
        }

    }

    /**
     *
     * @param req
     * @return
     */
    protected abstract String getCanceReason(Req req);

    protected abstract CourierOrderStateEnum getOrderState(Req req);

    @Override
    protected void afterHandle(Req req) {

    }
}
