package com.cmhq.core.api.strategy.apipush;

import com.cmhq.core.api.dto.request.StoPushSettleWeightDto;
import com.cmhq.core.api.dto.request.StoPushTraceDto;
import com.cmhq.core.enums.CourierWuliuStateEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * Created by Jiyang.Zheng on 2024/4/11 21:38.
 */
@Component
public class StoPushSettleWeightPush extends AbstartApiSettleWeightPush<StoPushSettleWeightDto>{


    @Override
    public ApiPushTypeEumn supports() {
        return ApiPushTypeEumn.TYPE_STO_PUSHSETTLEWEIGHT;
    }


    @Override
    protected Double getWeight(StoPushSettleWeightDto dto) {
        return StringUtils.isNotEmpty(dto.getSettleWeight()) ? Double.parseDouble(dto.getSettleWeight()) : 0d;
    }
}
