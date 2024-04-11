package com.cmhq.core.api.strategy.apipush;

import com.cmhq.core.api.dto.request.StoPushTraceDto;
import com.cmhq.core.enums.CourierWuliuStateEnum;
import org.springframework.stereotype.Component;

/**
 * Created by Jiyang.Zheng on 2024/4/11 21:38.
 */
@Component
public class StoPushOrderTracePush extends AbstartApiTracePush<StoPushTraceDto>{

    @Override
    protected CourierWuliuStateEnum getTraceState(StoPushTraceDto dto) {
        return null;
    }

    @Override
    public ApiPushTypeEumn supports() {
        return ApiPushTypeEumn.TYPE_STO_PUSHTRACE;
    }

    @Override
    protected void afterHandle(StoPushTraceDto dto) {

    }
}
