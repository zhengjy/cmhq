package com.cmhq.core.fitler;

import com.alibaba.fastjson.JSONObject;
import com.cmhq.core.api.UploadResult;
import com.cmhq.core.api.UploadTypeEnum;
import com.cmhq.core.api.dto.response.JTCourierFreightChargeDto;
import com.cmhq.core.api.strategy.StrategyFactory;
import com.cmhq.core.api.strategy.Upload;
import com.cmhq.core.enums.CourierCompanyEnum;
import com.cmhq.core.model.FaCourierOrderEntity;
import com.cmhq.core.model.dto.FreightChargeDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Created by Jiyang.Zheng on 2024/4/20 10:29.
 */
@Slf4j
@Component
public class JTFreightFilter extends AbstractFreightFilter{
    @Override
    public FreightChargeDto doFreightHandle(FaCourierOrderEntity order) {
        Upload upload = StrategyFactory.getUpload(UploadTypeEnum.TYPE_JT_QUERY_FREIGHT_CHARGE);
        UploadResult uploadResult = upload.execute(order);
        if (uploadResult.getFlag()){
            JTCourierFreightChargeDto ccd = JSONObject.parseObject(uploadResult.getJsonMsg()+"", JTCourierFreightChargeDto.class);
            if (uploadResult.getJsonMsg() == null || ccd.getTotalPrice() == null){
            }else {
                log.info("获取J&T运费价格 {}",JSONObject.toJSONString(uploadResult.getJsonMsg()));
                FreightChargeDto dto = new FreightChargeDto();
                //原始价格set
                dto.setTotalPrice(ccd.getTotalPrice());
                dto.setCourierCompanyCode(CourierCompanyEnum.COMPANY_JT.getType());
                return dto;
            }
        }
        return null;
    }
}
