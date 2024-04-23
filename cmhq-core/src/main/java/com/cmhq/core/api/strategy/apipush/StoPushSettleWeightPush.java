package com.cmhq.core.api.strategy.apipush;

import com.cmhq.core.api.dto.request.StoPushSettleWeightDto;
import com.cmhq.core.api.dto.request.StoPushTraceDto;
import com.cmhq.core.enums.CourierWuliuStateEnum;
import com.cmhq.core.model.dto.SettleWeightDto;
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
    protected SettleWeightDto getSettleWeight(StoPushSettleWeightDto dto) {
        if (dto == null){
            return null;
        }
        SettleWeightDto sd = new SettleWeightDto();
        sd.setWaybillNo(dto.getBillCode());
        sd.setTotalFreight(dto.getSettleWeight());
        return sd;
    }
}
