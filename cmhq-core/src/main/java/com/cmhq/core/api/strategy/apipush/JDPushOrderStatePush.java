package com.cmhq.core.api.strategy.apipush;

import com.cmhq.core.api.dto.request.JDPushDto;
import com.cmhq.core.enums.CourierOrderStateEnum;
import com.lop.open.api.sdk.domain.ECAP.CommonQueryOrderApi.commonGetOrderStatusV1.CommonOrderStatusResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * Created by Jiyang.Zheng on 2024/4/11 21:38.
 */
@Component
public class JDPushOrderStatePush extends AbstartApiOrderPush<JDPushDto<CommonOrderStatusResponse>>{
    @Override
    protected String getCanceReason(JDPushDto<CommonOrderStatusResponse> param) {
        CommonOrderStatusResponse dto = param.getObj();
        if (dto.getStatusDesc() != null){
            return dto.getStatusDesc();
        }
        return "";
    }

    @Override
    protected String getCourierOrderState(JDPushDto<CommonOrderStatusResponse> stateDto) {
        CommonOrderStatusResponse dto = stateDto.getObj();
        if (dto != null) {
            return dto.getStatus();
        }
        return "";
    }

    @Override
    protected CourierOrderStateEnum getOrderState(JDPushDto<CommonOrderStatusResponse> stateDto) {
        CommonOrderStatusResponse dto = stateDto.getObj();
        //100（已接单）；390（已下发）；420（已揽收）；430（运输中）；440（派送中）；480（已拦截）；500（异常终止）；510（妥投）；530（拒收）；690（已取消）
        if (dto != null) {
            if (StringUtils.equals(dto.getStatus(),"100") || StringUtils.equals(dto.getStatus(),"390")){
                return CourierOrderStateEnum.STATE_1;
            }else if (StringUtils.equals(dto.getStatus(),"420") ||
                    StringUtils.equals(dto.getStatus(),"430") ||
                    StringUtils.equals(dto.getStatus(),"440")){
                return CourierOrderStateEnum.STATE_2;
            }else if (StringUtils.equals(dto.getStatus(),"480") ||
                    StringUtils.equals(dto.getStatus(),"500") ||
                    StringUtils.equals(dto.getStatus(),"530") ||
                    StringUtils.equals(dto.getStatus(),"690")
            ){
                return CourierOrderStateEnum.STATE_3;
            }
        }
        return null;
    }

    @Override
    protected void otherHandle(JDPushDto<CommonOrderStatusResponse> commonOrderStatusResponseJDPushDto) {

    }

    @Override
    public ApiPushTypeEumn supports() {
        return ApiPushTypeEumn.TYPE_JD_PUSHORDERSTATUS;
    }
}
