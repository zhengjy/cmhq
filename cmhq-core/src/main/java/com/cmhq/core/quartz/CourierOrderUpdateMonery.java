package com.cmhq.core.quartz;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cmhq.core.api.UploadResult;
import com.cmhq.core.api.UploadTypeEnum;
import com.cmhq.core.api.strategy.StrategyFactory;
import com.cmhq.core.api.strategy.Upload;
import com.cmhq.core.api.strategy.apipush.AbstartApiTracePush;
import com.cmhq.core.api.strategy.apipush.JDPushWuliuTracePush;
import com.cmhq.core.dao.FaCourierOrderDao;
import com.cmhq.core.dao.FaCourierOrderExtDao;
import com.cmhq.core.enums.CourierOrderStateEnum;
import com.cmhq.core.enums.MoneyConsumeEumn;
import com.cmhq.core.enums.MoneyConsumeMsgEumn;
import com.cmhq.core.enums.UserMoneyConsumeMsgEumn;
import com.cmhq.core.model.FaCompanyEntity;
import com.cmhq.core.model.FaCourierOrderEntity;
import com.cmhq.core.model.FaCourierOrderExtEntity;
import com.cmhq.core.model.dto.CreateCourierOrderResponseDto;
import com.cmhq.core.model.dto.FreightChargeDto;
import com.cmhq.core.model.param.CompanyMoneyParam;
import com.cmhq.core.model.param.UserMoneyParam;
import com.cmhq.core.service.FaCompanyMoneyService;
import com.cmhq.core.service.FaCompanyService;
import com.cmhq.core.service.FaCourierOrderService;
import com.cmhq.core.service.FaUserMoneyService;
import com.cmhq.core.service.domain.CreateCourierOrderDomain;
import com.cmhq.core.util.EstimatePriceUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service("courierOrderUpdateMonery")
public class CourierOrderUpdateMonery {
    @Autowired
    private FaCourierOrderDao faCourierOrderDao;
    @Autowired
    private FaCompanyMoneyService faCompanyMoneyService;
    @Autowired
    private FaCompanyService faCompanyService;
    @Autowired
    FaCourierOrderService faCourierOrderService;
    @Autowired
    FaCourierOrderExtDao faCourierOrderExtDao;
    @Resource(name = "JDPushWuliuTracePush")
    private AbstartApiTracePush jdPushWuliuTracePush;
    @Autowired
    FaUserMoneyService faUserMoneyService;

    public void updateMoney(){
        log.info("=====执行定时更新订单金额重量=====");
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime firstDayOfLastMonth = now.with(TemporalAdjusters.firstDayOfMonth()).minusMonths(1);
        LocalDateTime lastDayOfLastMonth = now.with(TemporalAdjusters.lastDayOfMonth()).minusMonths(1);


        String st = DateUtil.format(firstDayOfLastMonth, "yyyy-MM-dd mm:hh:ss");
        String et = DateUtil.format(lastDayOfLastMonth, "yyyy-MM-dd mm:hh:ss");
        LambdaQueryWrapper<FaCourierOrderEntity> lam = new LambdaQueryWrapper<>();
        lam.ge(FaCourierOrderEntity::getCreateTime, st)
                .le(FaCourierOrderEntity::getCreateTime,et)
                .in(FaCourierOrderEntity::getWuliuState,3,4);
        List<FaCourierOrderEntity> list = faCourierOrderDao.selectList(lam);

//        list.forEach(order ->{
//            String weightstr = jdPushWuliuTracePush.(req);
//            FaCompanyEntity faCompanyEntity = faCompanyService.selectById(order.getFaCompanyId());
//            FreightChargeDto price = EstimatePriceUtil.getPrice(order.getFromProv(),order.getToProv(),order.getFromCity(),order.getToCity(),traceWeight,faCompanyEntity.getRatio());
//            //插入记录  返还商户预估费用& 扣除商户金额
//            faCompanyMoneyService.saveRecord(new CompanyMoneyParam(1, MoneyConsumeEumn.CONSUM_1, MoneyConsumeMsgEumn.MSG_4,order.getEstimatePrice(),order.getFaCompanyId(),order.getId()+"",order.getCourierCompanyWaybillNo()));
//
//            try {
//                //会出现创建时间相同
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//            faCompanyMoneyService.saveRecord(new CompanyMoneyParam(2, MoneyConsumeEumn.CONSUM_3, MoneyConsumeMsgEumn.MSG_2,price.getTotalPrice(),order.getFaCompanyId(),order.getId()+"",order.getCourierCompanyWaybillNo()));
//            if (faCompanyEntity.getFUser() != null){
//                faUserMoneyService.saveRecord(new UserMoneyParam(1, UserMoneyConsumeMsgEumn.MSG_1,price.getTotalPrice(),order.getFaCompanyId(),faCompanyEntity.getFUser(),faCompanyEntity.getDistributionRatio(),order.getId()+"",order.getCourierCompanyWaybillNo()));
//            }
//            //更新实际费用和重量
//            order.setPrice(price.getTotalPrice());
//            order.setWeightto(traceWeight);
//            order.setIsJiesuan(1);
//            faCourierOrderDao.update(order, new LambdaQueryWrapper<FaCourierOrderEntity>().eq(FaCourierOrderEntity::getCourierCompanyWaybillNo,req.getUnKey()));
//        });


        log.info("=====执行定时更新订单金额重量结束=====");

    }


}
