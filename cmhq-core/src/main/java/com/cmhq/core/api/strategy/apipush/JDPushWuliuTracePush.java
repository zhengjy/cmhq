package com.cmhq.core.api.strategy.apipush;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cmhq.core.api.UploadResult;
import com.cmhq.core.api.UploadTypeEnum;
import com.cmhq.core.api.dto.request.JDPushTraceDto;
import com.cmhq.core.api.strategy.StrategyFactory;
import com.cmhq.core.api.strategy.Upload;
import com.cmhq.core.enums.CourierWuliuStateEnum;
import com.cmhq.core.model.FaCourierOrderEntity;
import com.lop.open.api.sdk.domain.ECAP.CommonQueryOrderApi.commonGetOrderInfoV1.CommonOrderInfoResponse;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * Created by Jiyang.Zheng on 2024/4/11 21:38.
 */
@Component
public class JDPushWuliuTracePush extends AbstartApiTracePush<JDPushTraceDto>{


    @Override
    protected CourierWuliuStateEnum getTraceState(JDPushTraceDto param) {
        if (param != null){
            JDPushTraceDto d = param;

            if (d.getCategoryName().contains("完成" )){
                return CourierWuliuStateEnum.STATE_3;
            }else if (d.getCategoryName().contains("派送")){
                return CourierWuliuStateEnum.STATE_2;
            }else if (d.getCategoryName().contains("运输") || d.getCategoryName().contains("揽收")){
                return CourierWuliuStateEnum.STATE_1;
            }else if (d.getCategoryName().contains("取消") ){
                return CourierWuliuStateEnum.STATE_4;
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
            return infoResponse.getCargoes().get(0).getWeight();
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
