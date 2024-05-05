package com.cmhq.core.api.strategy.apipush;

import com.cmhq.core.api.dto.request.JDPushTraceDto;
import com.cmhq.core.enums.CourierWuliuStateEnum;
import org.springframework.stereotype.Component;


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
            }else if (d.getScanType().contains("派送")){
                return CourierWuliuStateEnum.STATE_2;
            }else if (d.getScanType().contains("运输") || d.getScanType().contains("揽收")){
                return CourierWuliuStateEnum.STATE_1;
            }else if (d.getScanType().contains("取消") ){
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
//        if (param.getObj(JTPushTraceDto.class) != null){
//            JTPushTraceDto dto = param.getObj(JTPushTraceDto.class);
//            List<JTPushTraceDto.Detail> detailList = dto.getDetails();
//            return detailList.get(0).getWeight();
//        }
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
