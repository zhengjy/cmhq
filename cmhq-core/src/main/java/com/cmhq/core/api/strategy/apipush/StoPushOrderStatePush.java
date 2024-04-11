package com.cmhq.core.api.strategy.apipush;

import com.cmhq.core.api.dto.request.StoPushOrderStateDto;
import com.cmhq.core.enums.CourierOrderStateEnum;
import org.springframework.stereotype.Component;

/**
 * Created by Jiyang.Zheng on 2024/4/11 21:38.
 */
@Component
public class StoPushOrderStatePush  extends AbstartApiOrderPush<StoPushOrderStateDto>{
    @Override
    protected String getCanceReason(StoPushOrderStateDto stoPushOrderStateDto) {
        return null;
    }

    @Override
    protected CourierOrderStateEnum getOrderState(StoPushOrderStateDto stoPushOrderStateDto) {
        return null;
    }

    @Override
    public ApiPushTypeEumn supports() {
        return ApiPushTypeEumn.TYPE_STO_PUSHORDERSTATUS;
    }
}
