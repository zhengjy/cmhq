package com.cmhq.core.api.strategy.apipush;

import com.cmhq.core.api.dto.request.JDPushTraceDto;
import com.cmhq.core.enums.CourierOrderStateEnum;
import com.cmhq.core.enums.CourierWuliuStateEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**https://open.jdl.com/#/open-business-document/access-guide/267/54018
 * Created by Jiyang.Zheng on 2024/4/11 21:38.
 */
@Component
public class JDPushOrderStatePush extends AbstartApiOrderPush<JDPushTraceDto>{
    @Override
    protected String getCanceReason(JDPushTraceDto param) {

        return StringUtils.isEmpty(param.getCancelReason()) ? param.getCategoryName() :param.getCancelReason();
    }

    @Override
    protected String getCourierOrderState(JDPushTraceDto stateDto) {
        return stateDto.getOperationTitle();
    }

    @Override
    protected CourierOrderStateEnum getOrderState(JDPushTraceDto dto) {
        if (dto != null) {
            if (dto.getState().equals("200001") || dto.getState().equals("200053")){
                return CourierOrderStateEnum.STATE_2;
            }
             if (dto.getCategoryName().contains("揽收")){
                return CourierOrderStateEnum.STATE_1;
            }else if (dto.getCategoryName().contains("取消") || dto.getState().equals("200052")){
                return CourierOrderStateEnum.STATE_3;
            }
            if (dto.getCategoryName().contains("完成") || dto.getState().equals("10034") || dto.getState().equals("10035")){
                return CourierOrderStateEnum.STATE_2;
            }else if (dto.getCategoryName().contains("派送")){
                return CourierOrderStateEnum.STATE_2;
            }else if (dto.getCategoryName().contains("运输") ){
                return CourierOrderStateEnum.STATE_2;
            }
        }
        return null;
    }

    @Override
    protected void otherHandle(JDPushTraceDto commonOrderStatusResponseJDPushDto) {

    }

    @Override
    public ApiPushTypeEumn supports() {
        return ApiPushTypeEumn.TYPE_JD_PUSHORDERSTATUS;
    }
}
