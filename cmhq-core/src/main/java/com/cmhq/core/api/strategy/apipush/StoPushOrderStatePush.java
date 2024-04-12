package com.cmhq.core.api.strategy.apipush;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cmhq.core.api.dto.request.StoPushOrderStateDto;
import com.cmhq.core.enums.CourierOrderStateEnum;
import com.cmhq.core.model.FaCourierOrderEntity;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;

/**订单状态 1未调派业务员2已调派业务员3已揽收4已取件5已取消
 * Created by Jiyang.Zheng on 2024/4/11 21:38.
 */
@Component
public class StoPushOrderStatePush  extends AbstartApiOrderPush<StoPushOrderStateDto>{
    @Override
    protected String getCanceReason(StoPushOrderStateDto dto) {
        if (dto.getCancelInfo() != null){
            return dto.getCancelInfo().getReason();
        }
        if (dto.getReturnInfo() != null){
            return dto.getReturnInfo().getReason();
        }
        return "";
    }

    @Override
    protected CourierOrderStateEnum getOrderState(StoPushOrderStateDto stateDto) {
        if (stateDto.getChangeInfo() != null) {
            StoPushOrderStateDto.ChangeInfo changeInfo = stateDto.getChangeInfo();
            //订单调度状态（2-已调派/1-已分配/5-已完成/6-打回/4-已取消）
            if (StringUtils.equals(changeInfo.getStatus(),"2")){
                return CourierOrderStateEnum.STATE_2;
            }else if (StringUtils.equals(changeInfo.getStatus(),"4")){
                return CourierOrderStateEnum.STATE_5;
            }else if (StringUtils.equals(changeInfo.getStatus(),"5")){//TODO
                return CourierOrderStateEnum.STATE_4;
            }
        }
        if (stateDto.getCancelInfo() != null){
            return CourierOrderStateEnum.STATE_5;
        }
        if (stateDto.getReturnInfo() != null) {
            return CourierOrderStateEnum.STATE_5;
        }
        return null;
    }



    @Override
    public ApiPushTypeEumn supports() {
        return ApiPushTypeEumn.TYPE_STO_PUSHORDERSTATUS;
    }
}
