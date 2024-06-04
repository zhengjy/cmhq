package com.cmhq.core.api.strategy;

import com.alibaba.fastjson.JSONObject;
import com.cmhq.core.api.UploadTypeEnum;
import com.cmhq.core.model.FaCourierOrderEntity;
import com.cmhq.core.model.dto.CreateCourierOrderResponseDto;
import com.google.common.collect.Maps;
import com.lop.open.api.sdk.DefaultDomainApiClient;
import com.lop.open.api.sdk.domain.ECAP.CommonCreateOrderApi.commonCreateOrderV1.CommonCargoInfo;
import com.lop.open.api.sdk.domain.ECAP.CommonCreateOrderApi.commonCreateOrderV1.CommonCreateOrderRequest;
import com.lop.open.api.sdk.domain.ECAP.CommonCreateOrderApi.commonCreateOrderV1.CommonCreateOrderResponse;
import com.lop.open.api.sdk.domain.ECAP.CommonModifyCancelOrderApi.commonModifyOrderV1.CommonModifyCancelOrderRequest;
import com.lop.open.api.sdk.plugin.LopPlugin;
import com.lop.open.api.sdk.request.ECAP.EcapV1OrdersCreateLopRequest;
import com.lop.open.api.sdk.request.ECAP.EcapV1OrdersModifyLopRequest;
import com.lop.open.api.sdk.request.ECAP.EcapV1OrdersPrecheckLopRequest;
import com.lop.open.api.sdk.response.ECAP.EcapV1OrdersCreateLopResponse;
import com.lop.open.api.sdk.response.ECAP.EcapV1OrdersModifyLopResponse;
import com.lop.open.api.sdk.response.ECAP.EcapV1OrdersPrecheckLopResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Jiyang.Zheng on 2024/4/7 13:56.
 */
@Slf4j
@Component
public class JDCourierOrderModify extends AbstractJDUpload<FaCourierOrderEntity, JDUploadData<CommonModifyCancelOrderRequest>>{
    @Override
    public UploadTypeEnum supports() {
        return UploadTypeEnum.TYPE_JD_ORDER_MODIFY;
    }

    @Override
    protected Object doSend(JDUploadData<CommonModifyCancelOrderRequest> uploadData) throws Exception {
        CommonModifyCancelOrderRequest dto = uploadData.getRequest();
        DefaultDomainApiClient client = geDefaultDomainApiClient();
        EcapV1OrdersModifyLopRequest request = new EcapV1OrdersModifyLopRequest();
        request.setRequest(dto);
        LopPlugin lopPlugin = getProduceLopPlugin();
        request.addLopPlugin(lopPlugin);
        EcapV1OrdersModifyLopResponse rsp = client.execute(request);
        log.info("EcapV1OrdersModifyLopResponse = {}",JSONObject.toJSONString(rsp));
        return rsp.getResult();
    }


    @Override
    protected Object jsonMsgHandle(Object jsonMsg) {
        CommonCreateOrderResponse response = JSONObject.parseObject(JSONObject.toJSONString(jsonMsg), CommonCreateOrderResponse.class);
        CreateCourierOrderResponseDto rsp = new CreateCourierOrderResponseDto();
        rsp.setWaybillNo(response.getWaybillCode());
        rsp.setOrderNo(response.getOrderCode());
        return rsp;
    }

    @Override
    protected JDUploadData<CommonModifyCancelOrderRequest> getData(FaCourierOrderEntity param) throws RuntimeException {

        CommonModifyCancelOrderRequest dto = new CommonModifyCancelOrderRequest();
        dto.setWaybillCode(param.getCourierCompanyWaybillNo());
        dto.setOrderId(param.getOrderNo());
        if (StringUtils.isNotEmpty(param.getTakeGoodsTimeEnd())){
            SimpleDateFormat ft2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                dto.setPickupStartTime(ft2.parse(param.getTakeGoodsTime()));
                dto.setPickupEndTime(ft2.parse(param.getTakeGoodsTimeEnd()));
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }else {
            SimpleDateFormat ft2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                Date date = ft2.parse(param.getTakeGoodsTime());
                date.setTime(date.getTime() - 1000* 60);
                dto.setPickupStartTime(date);
                dto.setPickupEndTime(ft2.parse(param.getTakeGoodsTime()));
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }

        dto.setOrderOrigin(1);
        dto.setCustomerCode(getCustomerCode());

        JDUploadData<CommonModifyCancelOrderRequest> uploadData = new JDUploadData<>();

        uploadData.setUnKey2(param.getOrderNo());
        uploadData.setRequest(dto);
        return uploadData;
    }

}


