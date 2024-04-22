package com.cmhq.core.api.strategy.apipush;

import com.cmhq.core.api.dto.request.JTPushDto;
import com.cmhq.core.api.dto.request.JTPushTraceDto;
import com.cmhq.core.enums.CourierWuliuStateEnum;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by Jiyang.Zheng on 2024/4/11 21:38.
 */
@Component
public class JTPushWuliuTracePush extends AbstartApiTracePush<JTPushDto<JTPushTraceDto>>{

    @Override
    protected CourierWuliuStateEnum getTraceState(JTPushDto<JTPushTraceDto> param) {
        if (param.getObj() != null){
            JTPushTraceDto dto = param.getObj(JTPushTraceDto.class);
            JTPushTraceDto.Detail d = dto.getDetails().get(0);
            /**
             * 扫描类型
             * 1、快件揽收
             * 2、入仓扫描（停用）
             * 3、发件扫描
             * 4、到件扫描
             * 5、出仓扫描
             * 6、入库扫描
             * 7、代理点收入扫描
             * 8、快件取出扫描
             * 9、出库扫描
             * 10、快件签收
             * 11、问题件扫描
             * 12、安检扫描
             * 13、其他扫描
             */
            if (d.getScanType().equals("10")){
                return CourierWuliuStateEnum.STATE_3;
            }else if (d.getScanType().equals("1") ){
                return CourierWuliuStateEnum.STATE_2;
            }else if (d.getScanType().equals("3")){
                return CourierWuliuStateEnum.STATE_1;
            }else if (d.getScanType().equals("11") ){
                return CourierWuliuStateEnum.STATE_4;
            }

        }
        return null;
    }

    @Override
    protected String getCourierWuliuState(JTPushDto<JTPushTraceDto> param) {
        if (param.getObj() != null){
            JTPushTraceDto dto = param.getObj(JTPushTraceDto.class);
            List<JTPushTraceDto.Detail> detailList = dto.getDetails();
            return detailList.get(0).getScanType();
        }
        return "";
    }

    @Override
    public ApiPushTypeEumn supports() {
        return ApiPushTypeEumn.TYPE_JT_PUSHTRACE;
    }

    @Override
    protected void afterHandle(JTPushDto<JTPushTraceDto> dto) {
//        orderEntity.setQjTime();
    }

}
