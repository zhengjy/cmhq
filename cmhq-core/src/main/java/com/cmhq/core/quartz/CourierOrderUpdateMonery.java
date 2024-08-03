package com.cmhq.core.quartz;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cmhq.core.api.UploadResult;
import com.cmhq.core.api.UploadTypeEnum;
import com.cmhq.core.api.dto.response.ActualFeeInfoDto;
import com.cmhq.core.api.strategy.StrategyFactory;
import com.cmhq.core.api.strategy.Upload;
import com.cmhq.core.dao.FaCourierOrderDao;
import com.cmhq.core.dao.FaCourierOrderExtDao;
import com.cmhq.core.enums.*;
import com.cmhq.core.model.FaCourierOrderEntity;
import com.cmhq.core.service.FaCompanyMoneyService;
import com.cmhq.core.service.FaCompanyService;
import com.cmhq.core.service.FaCourierOrderService;
import com.cmhq.core.service.FaUserMoneyService;
import com.cmhq.core.service.domain.OrderJiesuanAfterCheckWeightDomain;
import com.cmhq.core.util.SpringApplicationUtils;
import com.lop.open.api.sdk.domain.ECAP.CommonQueryOrderApi.commonGetActualFeeInfoV1.CommonActualFeeInfoDetailResponse;
import com.lop.open.api.sdk.domain.ECAP.CommonQueryOrderApi.commonGetActualFeeInfoV1.CommonActualFeeResponse;
import com.lop.open.api.sdk.domain.ECAP.CommonQueryOrderApi.commonGetOrderInfoV1.CommonOrderInfoResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
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
                .in(FaCourierOrderEntity::getWuliuState,3,4)
//                .eq(FaCourierOrderEntity::getFaCompanyId,90)
        ;
        List<FaCourierOrderEntity> orderList = faCourierOrderDao.selectList(lam);
        for (FaCourierOrderEntity order : orderList){
            String weightstr = getWeight(order.getCourierCompanyWaybillNo());
            if (StringUtils.isEmpty(weightstr)){
                continue;
            }
            double traceWeight = Double.parseDouble(weightstr);
            traceWeight = BigDecimal.valueOf(traceWeight ).setScale(2, RoundingMode.HALF_UP).doubleValue();
            List<ActualFeeInfoDto> list = getActualFeeInfo(order.getCourierCompanyWaybillNo());
            //超长超重记录
            new OrderJiesuanAfterCheckWeightDomain(order,traceWeight,list).handle();
        }
        log.info("=====执行定时更新订单金额重量结束=====");

    }

    protected String getWeight(String billCode) {
        Upload upload = StrategyFactory.getUpload(UploadTypeEnum.TYPE_JD_QUERY_COURIER_ORDER_INFO);
        UploadResult uploadResult = upload.execute(billCode);
        if (!uploadResult.getFlag()) {
            throw new RuntimeException("查询京东订单信息失败:"+uploadResult.getErrorMsg());
        }
        CommonOrderInfoResponse infoResponse = JSONObject.parseObject(uploadResult.getJsonMsg()+"", CommonOrderInfoResponse.class);
        if (CollectionUtils.isNotEmpty(infoResponse.getCargoes())){
            //实际重量【按实际重量与体积重量（计泡重量）两者取最大值计算运费】
            String againVolume = infoResponse.getCargoes().get(0).getAgainVolume();
            Double weight = 0D;
            if (StringUtils.isNotEmpty(againVolume)){
                FaCourierOrderDao faCourierOrderDao = SpringApplicationUtils.getBean(FaCourierOrderDao.class);
                FaCourierOrderEntity orderEntity = faCourierOrderDao.selectOne(new LambdaQueryWrapper<FaCourierOrderEntity>().eq(FaCourierOrderEntity::getCourierCompanyWaybillNo,billCode));
                if (orderEntity.getOrderOrigin() == 4){
                    weight = Double.parseDouble(againVolume) / 6000;
                }else {
                    weight = Double.parseDouble(againVolume) / 8000;

                }
            }
            String againWeight = infoResponse.getCargoes().get(0).getAgainWeight();
            if (StringUtils.isNotEmpty(againWeight)){
                weight = Math.max(Double.parseDouble(againWeight),weight) ;
            }
            if (weight > 0){
                return weight+"";
            }
        }
        return "";
    }

    protected List<ActualFeeInfoDto> getActualFeeInfo(String billCode) {
        Upload upload = StrategyFactory.getUpload(Objects.requireNonNull(UploadTypeEnum.getMsgByCode(CourierCompanyEnum.COMPANY_JD.getType(), UploadTypeEnum.TYPE_JD_ORDER_ACTUAL_FEE.getCodeNickName())));
        UploadResult uploadResult = upload.execute(billCode);
        if (!uploadResult.getFlag()) {
            log.error("获取jd物流公司订单状态失败【{}】",uploadResult.getErrorMsg());
        }else {
            CommonActualFeeResponse response = JSONObject.parseObject(JSONObject.toJSONString(uploadResult.getJsonMsg()),CommonActualFeeResponse.class);
            if (CollectionUtils.isNotEmpty(response.getCommonActualFeeInfoDetails())){
                List<CommonActualFeeInfoDetailResponse> oc = response.getCommonActualFeeInfoDetails().stream()
                        .filter(v -> v.getFeeType().equals("kkcccc") ||  v.getFeeType().equals("QLHCF") ||  v.getFeeType().equals("QLBJ")).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(oc)) {
                    return oc.stream().map(v -> {
                        ActualFeeInfoDto dto1 = new ActualFeeInfoDto();
                        if (v.getFeeType().equals("kkcccc")) {
                            dto1.setFeeType(ActualFeeInfoDto.FEETYPE_CCCC);
                        } else if (v.getFeeType().equals("QLHCF")) {
                            dto1.setFeeType(ActualFeeInfoDto.FEETYPE_QLHCF);
                        } else if (v.getFeeType().equals("QLBJ")) {
                            dto1.setFeeType(ActualFeeInfoDto.FEETYPE_QLBJ);
                        }
                        dto1.setMoney(v.getMoney().doubleValue());
                        return dto1;
                    }).collect(Collectors.toList());
                }
            }

        }
        return null;
    }


}
