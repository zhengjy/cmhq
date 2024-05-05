package com.cmhq.core.quartz;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cmhq.core.api.JDResponse;
import com.cmhq.core.api.UploadResult;
import com.cmhq.core.api.UploadTypeEnum;
import com.cmhq.core.api.dto.request.JDPushDto;
import com.cmhq.core.api.strategy.StrategyFactory;
import com.cmhq.core.api.strategy.Upload;
import com.cmhq.core.api.strategy.apipush.ApiPushTypeEumn;
import com.cmhq.core.dao.FaCourierOrderDao;
import com.cmhq.core.dao.FaCourierOrderExtDao;
import com.cmhq.core.enums.CourierCompanyEnum;
import com.cmhq.core.model.FaCourierOrderEntity;
import com.cmhq.core.service.FaCourierOrderService;
import com.lop.open.api.sdk.domain.ECAP.CommonQueryOrderApi.commonGetOrderStatusV1.CommonOrderStatusResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * 京东订单状态获取
 */
@Slf4j
@Service("courierGetJDOrderStatusTask")
public class CourierGetJDOrderStatusTask {
    @Autowired
    private FaCourierOrderDao faCourierOrderDao;
    @Autowired
    FaCourierOrderService faCourierOrderService;
    @Autowired
    FaCourierOrderExtDao faCourierOrderExtDao;


    public void getJDOrderStatus(){
        log.info("=====执行定时京东订单状态获取=====");
        LambdaQueryWrapper<FaCourierOrderEntity> lam = new LambdaQueryWrapper<>();
        String sd = DateUtil.format(LocalDateTime.now().minusHours(24), "yyyy-MM-dd mm:hh:ss");
        String ed = DateUtil.format(LocalDateTime.now(), "yyyy-MM-dd mm:hh:ss");
        lam.ge(FaCourierOrderEntity::getCreateTime, sd)
                .le(FaCourierOrderEntity::getCreateTime,ed);
        lam.eq(FaCourierOrderEntity::getCourierCompanyCode, CourierCompanyEnum.COMPANY_JD.getType())
                .notIn(FaCourierOrderEntity::getIsJiesuan, 1)
        ;
        List<FaCourierOrderEntity> list = faCourierOrderDao.selectList(lam);
        if (CollectionUtils.isEmpty(list)){
            log.info("=====执行定时京东订单状态获取未查询到数据=====");
            return;
        }

        try {
            for (FaCourierOrderEntity order : list){
                handle(order);
            }
        }catch (Exception e){
            log.error("",e);
        }

        log.info("=====执行定时京东订单状态获取结束=====");

    }

    public void handle(FaCourierOrderEntity order){
        Upload upload = StrategyFactory.getUpload(Objects.requireNonNull(UploadTypeEnum.getMsgByCode(order.getCourierCompanyCode(), UploadTypeEnum.TYPE_JD_QUERY_COURIER_ORDER_STATUS.getCodeNickName())));
        UploadResult uploadResult = upload.execute(order);
        if (!uploadResult.getFlag()) {
            log.error("获取jd物流公司订单状态失败【{}】",uploadResult.getErrorMsg());
        }else {
            CommonOrderStatusResponse response = JSONObject.parseObject(JSONObject.toJSONString(uploadResult.getJsonMsg()),CommonOrderStatusResponse.class);
            JDPushDto<CommonOrderStatusResponse> dto = new JDPushDto<>();
            dto.setObj(response);
            dto.setUnKeyValue2(order.getOrderNo());
            JDResponse jdResponse = StrategyFactory.getApiPush(ApiPushTypeEumn.TYPE_JD_PUSHORDERSTATUS).jdPushHandle(dto);
            log.info("获取jd物流公司 courierCompanyWaybillNo【{}】 订单状态 rsp 【{}】",order.getCourierCompanyOrderNo(),jdResponse);
        }
    }

}
