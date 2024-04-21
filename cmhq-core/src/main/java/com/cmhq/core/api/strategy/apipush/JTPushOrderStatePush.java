package com.cmhq.core.api.strategy.apipush;

import com.cmhq.core.api.dto.request.JTPushDto;
import com.cmhq.core.api.dto.request.JTPushOrderStateDto;
import com.cmhq.core.enums.CourierOrderStateEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**订单状态 状态(“已调派业务员”,“已揽收”，“已取件”，“已取消”)
 * Created by Jiyang.Zheng on 2024/4/11 21:38.
 */
@Component
public class JTPushOrderStatePush extends AbstartApiOrderPush<JTPushDto<JTPushOrderStateDto>>{
    @Override
    protected String getCanceReason(JTPushDto<JTPushOrderStateDto> param) {
        JTPushOrderStateDto dto = param.getObj();
        if (dto.getReason() != null){
            return dto.getReason();
        }
        return "";
    }

    @Override
    protected CourierOrderStateEnum getOrderState(JTPushDto<JTPushOrderStateDto> stateDto) {
        JTPushOrderStateDto dto = stateDto.getObj();
        if (dto != null) {
            //订单调度状态（2-已调派/1-已分配/5-已完成/6-打回/4-已取消）
            if (StringUtils.equals(dto.getScanType(),"已调派业务员")){
                return CourierOrderStateEnum.STATE_2;
            }else if (StringUtils.equals(dto.getScanType(),"已揽收")){
                return CourierOrderStateEnum.STATE_3;
            }else if (StringUtils.equals(dto.getScanType(),"已取件")){
                return CourierOrderStateEnum.STATE_4;
            }else if (StringUtils.equals(dto.getScanType(),"已取消")){
                return CourierOrderStateEnum.STATE_5;
            }
        }
        return null;
    }

    @Override
    protected void otherHandle(JTPushDto<JTPushOrderStateDto> param) {
        JTPushOrderStateDto dto = param.getObj();
        //TODO 實際重量更新 重新計算預估價格
        if (StringUtils.isNotEmpty(dto.getWeight())){

        }
    }


    @Override
    public ApiPushTypeEumn supports() {
        return ApiPushTypeEumn.TYPE_JT_PUSHORDERSTATUS;
    }
}
