package com.cmhq.core.service.domain;

import com.cmhq.core.api.UploadResult;
import com.cmhq.core.api.UploadTypeEnum;
import com.cmhq.core.api.strategy.StrategyFactory;
import com.cmhq.core.api.strategy.Upload;
import com.cmhq.core.dao.FaCourierOrderDao;
import com.cmhq.core.model.FaCourierOrderEntity;
import com.cmhq.core.service.FaCourierOrderService;
import com.cmhq.core.util.SpringApplicationUtils;

import java.util.Date;
import java.util.Objects;

/**
 * Created by Jiyang.Zheng on 2024/4/11 10:31.
 */
public class CancelCourierOrderDomain {
    //内容
    private String content;
    //订单id
    Integer orderId;

    private FaCourierOrderDao faCourierOrderDao;
    private FaCourierOrderService faCourierOrderService;
    public CancelCourierOrderDomain(String content, Integer orderId) {
        this.content = content;
        this.orderId = orderId;
        faCourierOrderDao =SpringApplicationUtils.getBean(FaCourierOrderDao.class);
    }

    public void handle(){
        FaCourierOrderEntity entity = faCourierOrderDao.selectById(orderId);
        //校验状态
        //待取件
        if (Objects.equals(entity.getOrderState(),0)
                //正常状态
                && (entity.getCancelOrderState() == 0 || entity.getCancelOrderState() == 1 || entity.getCancelOrderState() == 3)
                //没有物流信息
                && entity.getWuliuState() == null &&
                //未取件
                Objects.equals( entity.getCourierOrderState(),1)){
        }else {
            throw new RuntimeException("当前订单状态不允许取消！");
        }
        FaCourierOrderEntity updateState = new FaCourierOrderEntity();
        updateState.setId(orderId);
        updateState.setCancelOrderState(-1);
        updateState.setCancelTime(new Date());
        updateState.setReason(content);
        updateState.setCancelType(1);
        entity.setReason(content);
        faCourierOrderDao.updateById(updateState);
    }
}
