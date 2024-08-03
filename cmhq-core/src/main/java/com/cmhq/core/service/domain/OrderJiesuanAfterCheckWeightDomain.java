package com.cmhq.core.service.domain;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cmhq.core.api.dto.response.ActualFeeInfoDto;
import com.cmhq.core.dao.FaCourierOrderDao;
import com.cmhq.core.enums.MoneyConsumeEumn;
import com.cmhq.core.enums.MoneyConsumeMsgEumn;
import com.cmhq.core.model.FaCompanyEntity;
import com.cmhq.core.model.FaCourierOrderEntity;
import com.cmhq.core.model.dto.FreightChargeDto;
import com.cmhq.core.model.param.CompanyMoneyParam;
import com.cmhq.core.service.FaCompanyMoneyService;
import com.cmhq.core.service.FaCompanyService;
import com.cmhq.core.service.FaCourierOrderService;
import com.cmhq.core.util.EstimatePriceUtil;
import com.cmhq.core.util.SpringApplicationUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
public class OrderJiesuanAfterCheckWeightDomain {

    private FaCourierOrderEntity order;
    private Double weight;
    private List<ActualFeeInfoDto> feeInfoDtoList;
    public OrderJiesuanAfterCheckWeightDomain(FaCourierOrderEntity order, Double weight, List<ActualFeeInfoDto> feeInfoDtoList){
        this.order = order;
        this.weight = weight;
        this.feeInfoDtoList = feeInfoDtoList;

    }

    public void handle(){
        FaCompanyService faCompanyService = SpringApplicationUtils.getBean(FaCompanyService.class);
        FaCompanyMoneyService faCompanyMoneyService = SpringApplicationUtils.getBean(FaCompanyMoneyService.class);
        FaCourierOrderDao faCourierOrderDao = SpringApplicationUtils.getBean(FaCourierOrderDao.class);
        FaCourierOrderService faCourierOrderService = SpringApplicationUtils.getBean(FaCourierOrderService.class);
        if (weight == null){
            return;
        }
        double traceWeight = weight;
        traceWeight = BigDecimal.valueOf(traceWeight ).setScale(2, RoundingMode.HALF_UP).doubleValue();
        if (order.getWeightto().equals(traceWeight) ){
            return;
        }
        log.info("签收后发生重量变化订单 快递单号 {}， 原重量 {}，变更重量 {}",order.getCourierCompanyWaybillNo(),order.getWeightto(),traceWeight);
        FaCompanyEntity faCompanyEntity = faCompanyService.selectById(order.getFaCompanyId());
        FreightChargeDto price = EstimatePriceUtil.getPrice(order.getFromProv(),order.getToProv(),order.getFromCity(),order.getToCity(),traceWeight,faCompanyEntity.getRatio());
        //插入记录  返还商户预估费用& 扣除商户金额
        faCompanyMoneyService.saveRecord(new CompanyMoneyParam(1, MoneyConsumeEumn.CONSUM_1, MoneyConsumeMsgEumn.MSG_13,order.getPrice(),order.getFaCompanyId(),order.getId()+"",order.getCourierCompanyWaybillNo()));
        try {
            //会出现创建时间相同
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        faCompanyMoneyService.saveRecord(new CompanyMoneyParam(2, MoneyConsumeEumn.CONSUM_3, MoneyConsumeMsgEumn.MSG_2,price.getTotalPrice(),order.getFaCompanyId(),order.getId()+"",order.getCourierCompanyWaybillNo()));
        //更新实际费用和重量
        FaCourierOrderEntity newOrder = new FaCourierOrderEntity();
        newOrder.setPrice(price.getTotalPrice());
        newOrder.setWeightto(traceWeight);
        newOrder.setIsJiesuan(1);

        double difference = Math.abs(traceWeight-order.getWeight());
        //重量差距超过30kg，则物流公司重量可能不准则置为异常
        if (difference > 50){
            log.error("{} 物流公司重量差距超过30kg",order.getCourierCompanyWaybillNo());
        }else {
            newOrder.setOrderIsError(1);
        }
        faCourierOrderDao.update(newOrder, new LambdaQueryWrapper<FaCourierOrderEntity>().eq(FaCourierOrderEntity::getCourierCompanyWaybillNo,order.getCourierCompanyWaybillNo()));

        //超长超重记录
        List<ActualFeeInfoDto> list = feeInfoDtoList;
        if (CollectionUtils.isNotEmpty(list)){
            faCourierOrderService.saveOrderExt(order.getId(),"order_fee_type",list.stream().map(ActualFeeInfoDto::getFeeType).collect(Collectors.joining(",")));
            faCourierOrderService.saveOrderExt(order.getId(),"order_fee_money",list.stream().map(v -> v.getMoney()+"").collect(Collectors.joining(",")));
            Double d = 0D;
            for (ActualFeeInfoDto dto : list){
                MoneyConsumeMsgEumn eumn = null;
                MoneyConsumeMsgEumn eumn2 = null;
                if (dto != null && dto.getFeeType().equals(ActualFeeInfoDto.FEETYPE_CCCC)){
                    eumn = MoneyConsumeMsgEumn.MSG_10;
                    eumn2 = MoneyConsumeMsgEumn.MSG_14;
                }else if (dto != null && dto.getFeeType().equals(ActualFeeInfoDto.FEETYPE_QLHCF)){
                    eumn = MoneyConsumeMsgEumn.MSG_12;
                    eumn2 = MoneyConsumeMsgEumn.MSG_15;
                }
                if (eumn != null){
                    d = d+ dto.getMoney();
                    faCompanyMoneyService.saveRecord(new CompanyMoneyParam(1, MoneyConsumeEumn.CONSUM_1, eumn2,dto.getMoney(),order.getFaCompanyId(),order.getId()+"",order.getCourierCompanyWaybillNo()));
                    faCompanyMoneyService.saveRecord(new CompanyMoneyParam(2, MoneyConsumeEumn.CONSUM_3, eumn,dto.getMoney(),order.getFaCompanyId(),order.getId()+"",order.getCourierCompanyWaybillNo()));
                }
            }
            FaCourierOrderEntity pe = new FaCourierOrderEntity();
            pe.setPrice(order.getPrice()+d);
            faCourierOrderDao.update(pe, new LambdaQueryWrapper<FaCourierOrderEntity>().eq(FaCourierOrderEntity::getCourierCompanyWaybillNo,order.getCourierCompanyWaybillNo()));
        }
    }
}
