package com.cmhq.core.api.strategy.apipush;

import com.cmhq.core.api.dto.request.StoPushTraceDto;
import com.cmhq.core.enums.CourierWuliuStateEnum;
import com.cmhq.core.model.FaCourierOrderEntity;
import org.springframework.stereotype.Component;

/**
 * Created by Jiyang.Zheng on 2024/4/11 21:38.
 */
@Component
public class StoPushWuliuTracePush extends AbstartApiTracePush<StoPushTraceDto>{

    @Override
    protected CourierWuliuStateEnum getTraceState(StoPushTraceDto dto) {
        if (dto.getTrace() != null){
            //扫描类型（收件;发件;到件;派件;第三方代派;问题件;退回件;留仓件;快件取出;代取快递; 派件入柜;柜机代收;驿站代收;第三方代收;签收;改地址件(2020-07-24 新增)）
            StoPushTraceDto.Trace trace = dto.getTrace();
            if (trace.equals("签收") || trace.equals("快件取出")){
                return CourierWuliuStateEnum.STATE_3;
            }else if (trace.equals("派件入柜") || trace.equals("派件")){
                return CourierWuliuStateEnum.STATE_2;
            }else if (trace.equals("发件") || trace.equals("收件")){
                return CourierWuliuStateEnum.STATE_1;
            }

        }
        return null;
    }

    @Override
    public ApiPushTypeEumn supports() {
        return ApiPushTypeEumn.TYPE_STO_PUSHTRACE;
    }

    @Override
    protected void afterHandle(StoPushTraceDto dto) {
//        orderEntity.setQjTime();
    }

}