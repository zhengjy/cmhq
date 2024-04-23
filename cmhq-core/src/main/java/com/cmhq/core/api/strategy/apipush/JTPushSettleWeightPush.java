package com.cmhq.core.api.strategy.apipush;

import com.cmhq.core.api.dto.request.JTPushDto;
import com.cmhq.core.api.dto.request.JTPushSettleWeightDto;
import com.cmhq.core.model.dto.SettleWeightDto;
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
    protected SettleWeightDto getSettleWeight(JTPushDto<JTPushSettleWeightDto> dto) {
        if (dto == null){
            return null;
        }
        JTPushSettleWeightDto dd = dto.getObj(JTPushSettleWeightDto.class);
        SettleWeightDto sd = new SettleWeightDto();
        sd.setWaybillNo(dd.getWaybillNo());
        sd.setTotalFreight(dd.getTotalFreight());
        sd.setPackageChargeWeight(dd.getPackageChargeWeight());
        return sd;
    }

    @Override
    protected void afterHandle(JTPushDto<JTPushSettleWeightDto> dto) {
        super.afterHandle(dto);
    }
}
