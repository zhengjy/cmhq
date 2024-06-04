package com.cmhq.core.api.strategy.apipush;

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
    protected String getIsErrorMsg(JDPushTraceDto param) {

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
