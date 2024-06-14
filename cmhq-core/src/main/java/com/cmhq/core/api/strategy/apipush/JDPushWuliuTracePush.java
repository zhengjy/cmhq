package com.cmhq.core.api.strategy.apipush;

import com.cmhq.core.api.dto.response.ActualFeeInfoDto;
import com.cmhq.core.enums.CourierCompanyEnum;
import com.cmhq.core.model.FaCourierOrderEntity;
import com.cmhq.core.service.domain.ReturnOrderCreateCourierOrderDomain;
import com.lop.open.api.sdk.domain.ECAP.CommonQueryOrderApi.commonGetActualFeeInfoV1.CommonActualFeeInfoDetailResponse;
import com.lop.open.api.sdk.domain.ECAP.CommonQueryOrderApi.commonGetActualFeeInfoV1.CommonActualFeeResponse;
import org.apache.commons.lang3.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.cmhq.core.api.UploadResult;
import com.cmhq.core.api.UploadTypeEnum;
import com.cmhq.core.api.dto.request.JDPushTraceDto;
import com.cmhq.core.api.strategy.StrategyFactory;
import com.cmhq.core.api.strategy.Upload;
import com.cmhq.core.enums.CourierWuliuStateEnum;
import com.lop.open.api.sdk.domain.ECAP.CommonQueryOrderApi.commonGetOrderInfoV1.CommonOrderInfoResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


/**京东物流状态推送 物流和订单状态同一个接口
 * Created by Jiyang.Zheng on 2024/4/11 21:38.
 */
@Slf4j
@Component
public class JDPushWuliuTracePush extends AbstartApiTracePush<JDPushTraceDto>{


    @Override
    protected CourierWuliuStateEnum getTraceState(JDPushTraceDto param) {
        if (param != null){
            JDPushTraceDto d = param;
            JDPushTraceDto orderDto = new JDPushTraceDto();

            BeanUtils.copyProperties(param,orderDto);
            orderDto.setWaybillCode(param.getOrderId());
            StrategyFactory.getApiPush(ApiPushTypeEumn.TYPE_JD_PUSHORDERSTATUS).jdPushHandle(orderDto);


            if (d.getCategoryName().contains("完成") || d.getState().equals("10034")){
                return CourierWuliuStateEnum.STATE_3;
            }else if (d.getCategoryName().contains("派送")
                    || d.getState().equals("11000")
                    || d.getState().equals("200023")
                    || d.getState().equals("200003")
            ){
                return CourierWuliuStateEnum.STATE_2;
            }else if (d.getCategoryName().contains("运输") ){
                return CourierWuliuStateEnum.STATE_1;
            }else if (d.getState().equals("10035")){
                return CourierWuliuStateEnum.STATE_4;
            }else if (d.getState().equals("200020")){
                return CourierWuliuStateEnum.STATE_5;
            }

        }
        return null;
    }

    @Override
    protected String getCourierWuliuState(JDPushTraceDto param) {
        return param.getOperationTitle();
    }

    @Override
    protected String getWeight(JDPushTraceDto param) {
        Upload upload = StrategyFactory.getUpload(UploadTypeEnum.TYPE_JD_QUERY_COURIER_ORDER_INFO);
        UploadResult uploadResult = upload.execute(param.getWaybillCode());
        if (!uploadResult.getFlag()) {
            throw new RuntimeException("查询京东订单信息失败:"+uploadResult.getErrorMsg());
        }
        CommonOrderInfoResponse infoResponse = JSONObject.parseObject(uploadResult.getJsonMsg()+"", CommonOrderInfoResponse.class);
        if (CollectionUtils.isNotEmpty(infoResponse.getCargoes())){
            //实际重量【按实际重量与体积重量（计泡重量）两者取最大值计算运费】
            String againVolume = infoResponse.getCargoes().get(0).getAgainVolume();
            Double weight = 0D;
            if (StringUtils.isNotEmpty(againVolume)){
                weight = Double.parseDouble(againVolume) / 8000;
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

    @Override
    protected List<ActualFeeInfoDto> getActualFeeInfo(JDPushTraceDto dto) {
        Upload upload = StrategyFactory.getUpload(Objects.requireNonNull(UploadTypeEnum.getMsgByCode(CourierCompanyEnum.COMPANY_JD.getType(), UploadTypeEnum.TYPE_JD_ORDER_ACTUAL_FEE.getCodeNickName())));
        UploadResult uploadResult = upload.execute(dto.getUnKeyValue());
        if (!uploadResult.getFlag()) {
            log.error("获取jd物流公司订单状态失败【{}】",uploadResult.getErrorMsg());
        }else {
            CommonActualFeeResponse response = JSONObject.parseObject(JSONObject.toJSONString(uploadResult.getJsonMsg()),CommonActualFeeResponse.class);
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
        return null;
    }

    @Override
    protected String getIsErrorMsg(JDPushTraceDto param) {

        return "";
    }

    @Override
    protected String getRetWaybillNo(JDPushTraceDto dto) {
        String input = "新运单号JDVC24498388029";
        String regex = "JDVC\\d+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            log.error("退单提取到运单号 【{}】",JSONObject.toJSONString(dto));
            return matcher.group();
        } else {
            log.error("未能提取到运单号 【{}】",JSONObject.toJSONString(dto));
        }
        return "";
    }

    @Override
    public ApiPushTypeEumn supports() {
        return ApiPushTypeEumn.TYPE_JD_PUSHTRACE;
    }

    @Override
    protected void afterHandle(JDPushTraceDto dto) {
//        orderEntity.setQjTime();
    }

}
