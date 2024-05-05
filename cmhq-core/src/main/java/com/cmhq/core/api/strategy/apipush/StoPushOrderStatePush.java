package com.cmhq.core.api.strategy.apipush;

import com.cmhq.core.api.dto.request.StoPushOrderStateDto;
import com.cmhq.core.enums.CourierOrderStateEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;


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
    protected String getCourierOrderState(StoPushOrderStateDto stateDto) {
        if (stateDto.getChangeInfo() != null) {
            StoPushOrderStateDto.ChangeInfo changeInfo = stateDto.getChangeInfo();
            return changeInfo.getStatus();
        }
        return "";
    }

    @Override
    protected CourierOrderStateEnum getOrderState(StoPushOrderStateDto stateDto) {
        if (stateDto.getChangeInfo() != null) {
            StoPushOrderStateDto.ChangeInfo changeInfo = stateDto.getChangeInfo();
            //订单调度状态（2-已调派/1-已分配/5-已完成/6-打回/4-已取消）
            if (StringUtils.equals(changeInfo.getStatus(),"2") || StringUtils.equals(changeInfo.getStatus(),"1")){
                return CourierOrderStateEnum.STATE_1;
            }else if (StringUtils.equals(changeInfo.getStatus(),"5")){
                return CourierOrderStateEnum.STATE_2;
            }else if (StringUtils.equals(changeInfo.getStatus(),"6") || StringUtils.equals(changeInfo.getStatus(),"4")){
                return CourierOrderStateEnum.STATE_3;
            }
        }
        if (stateDto.getCancelInfo() != null){
            return CourierOrderStateEnum.STATE_3;
        }
        if (stateDto.getReturnInfo() != null) {
            return CourierOrderStateEnum.STATE_3;
        }
        return null;
    }

    @Override
    protected void otherHandle(StoPushOrderStateDto stoPushOrderStateDto) {

    }


    @Override
    public ApiPushTypeEumn supports() {
        return ApiPushTypeEumn.TYPE_STO_PUSHORDERSTATUS;
    }
}
