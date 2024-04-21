package com.cmhq.core.api.strategy.apipush;

import com.cmhq.core.api.dto.request.JTPushDto;
import com.cmhq.core.api.dto.request.JTPushSettleWeightDto;
import org.springframework.stereotype.Component;

/**
 * Created by Jiyang.Zheng on 2024/4/11 21:38.
 */
@Component
public class JTPushSettleWeightPush extends AbstartApiSettleWeightPush<JTPushDto<JTPushSettleWeightDto>>{


    @Override
    public ApiPushTypeEumn supports() {
        return ApiPushTypeEumn.TYPE_JT_PUSHSETTLEWEIGHT;
    }


    @Override
    protected Double getWeight(JTPushDto<JTPushSettleWeightDto> dto) {
        if (dto.getObj() != null){
            return Double.parseDouble(dto.getObj().getPackageChargeWeight());
        }
        return 0D;
    }

    @Override
    protected void afterHandle(JTPushDto<JTPushSettleWeightDto> dto) {
        super.afterHandle(dto);
    }
}
