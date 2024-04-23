package com.cmhq.core.api.strategy.apipush;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cmhq.core.api.UploadData;
import com.cmhq.core.enums.CourierWuliuStateEnum;
import com.cmhq.core.enums.MoneyConsumeEumn;
import com.cmhq.core.enums.MoneyConsumeMsgEumn;
import com.cmhq.core.model.CompanyMoneyParam;
import com.cmhq.core.model.FaCourierOrderEntity;
import com.cmhq.core.model.dto.SettleWeightDto;
import lombok.Data;
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
        SettleWeightDto swd = getSettleWeight(req);
        if (swd == null){
            log.error("未返回结算运费信息 params 【{}】", JSONObject.toJSONString(order));
            return;
        }
        double weight = order.getWeight() == null ? 0D : order.getWeight();
        //返回的和填写的出入比例 达到商户配置,则记录 ,触发则冻结

        //重新计算运费 +  返回重量

        //增加新的冻结

        //更新实际重量

        swd.getWaybillNo();
        //

    }

    protected abstract SettleWeightDto getSettleWeight(Req req);

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
