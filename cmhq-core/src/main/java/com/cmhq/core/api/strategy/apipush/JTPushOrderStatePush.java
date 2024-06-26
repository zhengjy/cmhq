package com.cmhq.core.api.strategy.apipush;

import com.cmhq.core.api.dto.request.JTPushDto;
import com.cmhq.core.api.dto.request.JTPushOrderStateDto;
import com.cmhq.core.api.dto.request.JTPushTraceDto;
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
        JTPushOrderStateDto dto = param.getObj(JTPushOrderStateDto.class);
        if (dto.getReason() != null){
            return dto.getReason();
        }
        return "";
    }

    @Override
    protected String getCourierOrderState(JTPushDto<JTPushOrderStateDto> stateDto) {
        JTPushOrderStateDto dto = stateDto.getObj(JTPushOrderStateDto.class);
        if (dto != null) {
            return dto.getScanType();
        }
        return "";
    }

    @Override
    protected CourierOrderStateEnum getOrderState(JTPushDto<JTPushOrderStateDto> stateDto) {
        JTPushOrderStateDto dto = stateDto.getObj(JTPushOrderStateDto.class);
        //订单状态 状态(“已调派业务员”,“已揽收”，“已取件”，“已取消”)
        if (dto != null) {
            if (StringUtils.equals(dto.getScanType(),"已调派业务员")){
                return CourierOrderStateEnum.STATE_1;
            }else if (StringUtils.equals(dto.getScanType(),"已揽收") || StringUtils.equals(dto.getScanType(),"已取件")){
                return CourierOrderStateEnum.STATE_2;
            }else if (StringUtils.equals(dto.getScanType(),"已取消")){
                return CourierOrderStateEnum.STATE_3;
            }
        }
        return null;
    }

    @Override
    protected void otherHandle(JTPushDto<JTPushOrderStateDto> param) {
        JTPushOrderStateDto dto = param.getObj(JTPushOrderStateDto.class);
        //TODO 實際重量更新 重新計算預估價格
        if (StringUtils.isNotEmpty(dto.getWeight())){
            //更新余额 & 预扣费 & 插入记录

        }
    }


    @Override
    public ApiPushTypeEumn supports() {
        return ApiPushTypeEumn.TYPE_JT_PUSHORDERSTATUS;
    }
}
