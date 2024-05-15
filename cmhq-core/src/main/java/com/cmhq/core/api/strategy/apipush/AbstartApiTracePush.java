package com.cmhq.core.api.strategy.apipush;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cmhq.core.api.UploadData;
import com.cmhq.core.dao.FaCompanyDao;
import com.cmhq.core.enums.CourierWuliuStateEnum;
import com.cmhq.core.enums.MoneyConsumeEumn;
import com.cmhq.core.enums.MoneyConsumeMsgEumn;
import com.cmhq.core.fitler.FreightFilter;
import com.cmhq.core.model.CompanyMoneyParam;
import com.cmhq.core.model.FaCompanyDayOpeNumEntity;
import com.cmhq.core.model.FaCompanyEntity;
import com.cmhq.core.model.FaCourierOrderEntity;
import com.cmhq.core.model.dto.FreightChargeDto;
import com.cmhq.core.service.FaCompanyDayOpeNumService;
import com.cmhq.core.service.FaCompanyService;
import com.cmhq.core.util.EstimatePriceUtil;
import com.cmhq.core.util.SpringApplicationUtils;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import net.dreamlu.mica.core.spring.SpringContextUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

/**
 * 物流状态处理
 * Created by Jiyang.Zheng on 2024/4/11 21:03.
 */
@Slf4j
@Component
public abstract class AbstartApiTracePush< Req extends UploadData> extends AbstractApiPush<Req> {
    @Resource(name = "jTFreightFilter")
    private FreightFilter freightFilter;
    @Autowired
    FaCompanyService faCompanyService;
    @Autowired
    FaCompanyDayOpeNumService faCompanyDayOpeNumService;
    @Override
    public void doPushHandle(Req req) {
        FaCourierOrderEntity order = getFaCourierOrder(req);
        if (order == null){
            log.error("为查询到订单 params 【{}】", JSONObject.toJSONString(req));
            return;
        }
        if (order.getWuliuState() != null && order.getWuliuState().toString().equals(CourierWuliuStateEnum.STATE_3.getType())){
            log.error("订单已签收 params 【{}】", JSONObject.toJSONString(req));
            return;
        }
        //获取状态
        CourierWuliuStateEnum wuliuStateEnum = getTraceState(req);
        if (wuliuStateEnum == null){
            log.error("未匹配到物流状态 params 【{}】", JSONObject.toJSONString(req));
            return;
        }
        //获取状态
        FaCourierOrderEntity orderEntity = new FaCourierOrderEntity();
        orderEntity.setWuliuState(Integer.parseInt(wuliuStateEnum.getType()));
        orderEntity.setCourierWuliuState(getCourierWuliuState(req));
        if (wuliuStateEnum.getType().equals(CourierWuliuStateEnum.STATE_4.getType())){
            orderEntity.setOrderIsError(0);
            faCourierOrderService.saveOrderExt(order.getId(),"orderIsErrorMsg",getIsErrorMsg(req));
        }
        if (wuliuStateEnum.getType().equals(CourierWuliuStateEnum.STATE_3.getType())){
            //签收处理
            doHandle(order, req);
        }
        faCourierOrderDao.update(orderEntity, new LambdaQueryWrapper<FaCourierOrderEntity>().eq(FaCourierOrderEntity::getCourierCompanyWaybillNo,req.getUnKey()));
    }

    private void doHandle(FaCourierOrderEntity order,Req req){
        FaCompanyEntity faCompanyEntity = faCompanyService.selectById(order.getFaCompanyId());
        if (StringUtils.isNotEmpty(getWeight(req))){
            double traceWeight = Double.parseDouble(getWeight(req));
            double weight = order.getWeight() == null ? 0D : order.getWeight();
            //计算是否超过商户配置的比例
            saveRecord(order,faCompanyEntity,traceWeight,weight,req);

            FreightChargeDto price = EstimatePriceUtil.getPrice(order.getFromProv(),order.getToProv(),order.getFromCity(),order.getToCity(),traceWeight,faCompanyEntity.getRatio());
            //插入记录  返还商户预估费用& 扣除商户金额
            faCompanyMoneyService.saveRecord(new CompanyMoneyParam(1, MoneyConsumeEumn.CONSUM_1, MoneyConsumeMsgEumn.MSG_4,order.getEstimatePrice(),order.getFaCompanyId(),order.getId()+"",order.getCourierCompanyWaybillNo()));
            try {
                //会出现创建时间相同
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            faCompanyMoneyService.saveRecord(new CompanyMoneyParam(2, MoneyConsumeEumn.CONSUM_3, MoneyConsumeMsgEumn.MSG_2,price.getTotalPrice(),order.getFaCompanyId(),order.getId()+"",order.getCourierCompanyWaybillNo()));
            //更新实际费用和重量
            order.setPrice(price.getTotalPrice());
            order.setWeightto(traceWeight);
            order.setIsJiesuan(1);
            faCourierOrderDao.update(order, new LambdaQueryWrapper<FaCourierOrderEntity>().eq(FaCourierOrderEntity::getCourierCompanyWaybillNo,req.getUnKey()));
        }
    }

    /**
     * 记录是否触发 填写的重量和实际重量相差是否大于配置商户的触发比例
     * @param order
     * @param faCompanyEntity
     * @param traceWeight
     * @param weight
     */
    private void saveRecord(FaCourierOrderEntity order,FaCompanyEntity faCompanyEntity,double traceWeight,double weight,Req req){
        double difference = traceWeight - weight;
        //判断下单时填写的重量和实际重量相差是否大于配置商户的触发比例
        double percentDifference = new BigDecimal((difference / weight ) * 100).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        if ( percentDifference > faCompanyEntity.getCheckWeightRatio()){
            //增加新的触发冻结记录
            faCompanyDayOpeNumService.updateValue(LocalDate.now().toString(), FaCompanyDayOpeNumEntity.TYPE_CHECK_WEIGHT_RATIO_NUM, 1,order.getFaCompanyId());
        }
        if (StringUtils.isNotEmpty(faCompanyEntity.getCheckWeightType())){
            String sd = "";
            String ed = "";
            if (StringUtils.equals(faCompanyEntity.getCheckWeightType(), "day")){
                sd = LocalDate.now().toString();
                ed = sd;
            }else if (StringUtils.equals(faCompanyEntity.getCheckWeightType(),"month")){
                sd = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()).toString();
                ed = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth()).toString();
            }else if (StringUtils.equals(faCompanyEntity.getCheckWeightType(),"week")){
                sd = LocalDate.now().with(DayOfWeek.MONDAY).toString();
                ed = LocalDate.now().with(DayOfWeek.SUNDAY).toString();
            }
            //校验当前商户
            Integer companyId = order.getFaCompanyId();
            double checkWeightRatioNum = faCompanyDayOpeNumService.selectValue(sd,ed, FaCompanyDayOpeNumEntity.TYPE_CHECK_WEIGHT_RATIO_NUM,companyId.longValue());
            //返回的和填写的出入比例 达到商户配置,则记录 ,触发则冻结
            if (checkWeightRatioNum >= faCompanyEntity.getCheckWeightNum()){
                FaCompanyEntity editCompany = new FaCompanyEntity();
                editCompany.setIsFreeze("Y");
                editCompany.setId(faCompanyEntity.getId());
                faCompanyService.edit(editCompany);
                log.error("触发重量和结算比例差异过大 params 【{}】", JSONObject.toJSONString(req));
            }
        }
    }


    /**
     *
     * @param req
     * @return
     */
    protected abstract CourierWuliuStateEnum getTraceState(Req req);
    protected abstract String getCourierWuliuState(Req req);
    protected abstract String getWeight(Req req);
    protected abstract String getIsErrorMsg(Req req);

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
