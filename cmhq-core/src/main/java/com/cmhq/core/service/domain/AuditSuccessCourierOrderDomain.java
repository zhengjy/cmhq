package com.cmhq.core.service.domain;

import com.cmhq.core.dao.FaCourierOrderDao;
import com.cmhq.core.enums.MoneyConsumeEumn;
import com.cmhq.core.enums.MoneyConsumeMsgEumn;
import com.cmhq.core.model.CompanyMoneyParam;
import com.cmhq.core.model.FaCourierOrderEntity;
import com.cmhq.core.service.FaCompanyMoneyService;
import com.cmhq.core.service.FaCourierOrderService;
import com.cmhq.core.util.SpringApplicationUtils;

import java.util.Objects;

/**
 * 审核通过
 * Created by Jiyang.Zheng on 2024/4/11 10:31.
 */
public class AuditSuccessCourierOrderDomain {
    //内容
    private String content;
    //订单id
    Integer orderId;

    private FaCourierOrderDao faCourierOrderDao;
    private FaCourierOrderService faCourierOrderService;
    private final FaCompanyMoneyService faCompanyMoneyService;
    public AuditSuccessCourierOrderDomain(String content, Integer orderId) {
        this.content = content;
        this.orderId = orderId;
        faCourierOrderDao =SpringApplicationUtils.getBean(FaCourierOrderDao.class);
        faCompanyMoneyService = SpringApplicationUtils.getBean(FaCompanyMoneyService.class);
    }

    public void handle(){
        FaCourierOrderEntity order = faCourierOrderDao.selectById(orderId);
        //校验状态
        //待取件
        if (Objects.equals(order.getOrderState(),0)
                //正常状态
                &&  Objects.equals(order.getCancelOrderState(),-1)
                //没有物流信息
                && order.getWuliuState() == null &&
                //未取件
                Objects.equals( order.getCourierOrderState(),1)){
        }else {
            throw new RuntimeException("当前订单状态不允许审核！");
        }

        FaCourierOrderEntity state = new FaCourierOrderEntity();
        state.setId(orderId);
        state.setCancelOrderState(2);

        //更新审核通过状态
        faCourierOrderDao.updateById(state);
        double estimatePrice = order.getEstimatePrice();
        //插入返还记录 &返还商户或扣除商户金额
        faCompanyMoneyService.saveRecord(new CompanyMoneyParam(1, MoneyConsumeEumn.CONSUM_1, MoneyConsumeMsgEumn.MSG_6,estimatePrice,order.getFaCompanyId(),order.getId()+""));
    }
}
