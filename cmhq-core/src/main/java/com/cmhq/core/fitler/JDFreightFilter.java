package com.cmhq.core.fitler;

import com.alibaba.fastjson.JSONObject;
import com.cmhq.core.api.UploadResult;
import com.cmhq.core.api.UploadTypeEnum;
import com.cmhq.core.api.strategy.StrategyFactory;
import com.cmhq.core.api.strategy.Upload;
import com.cmhq.core.enums.CourierCompanyEnum;
import com.cmhq.core.model.FaCourierOrderEntity;
import com.cmhq.core.model.dto.FreightChargeDto;
import com.lop.open.api.sdk.domain.ECAP.CommonCreateOrderApi.commonCheckPreCreateOrderV1.CommonCheckPreCreateOrderResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Created by Jiyang.Zheng on 2024/4/20 10:29.
 */
@Slf4j
@Component("jdFreightFilter")
public class JDFreightFilter extends AbstractFreightFilter{
    @Override
    public FreightChargeDto doFreightHandle(FaCourierOrderEntity order) {
        Upload upload = StrategyFactory.getUpload(UploadTypeEnum.TYPE_JD_QUERY_FREIGHT_CHARGE);
        UploadResult uploadResult = upload.execute(order);
        if (uploadResult.getFlag()){
            CommonCheckPreCreateOrderResponse ccd = JSONObject.parseObject(uploadResult.getJsonMsg()+"", CommonCheckPreCreateOrderResponse.class);
            if (ccd.getTotalFreightStandard() == null){
            }else {
                log.info("获取JD运费价格 {}",JSONObject.toJSONString(uploadResult.getJsonMsg()));
                FreightChargeDto dto = new FreightChargeDto();
                dto.setTotalPrice(ccd.getTotalFreightStandard().doubleValue());
                dto.setCourierCompanyCode(CourierCompanyEnum.COMPANY_JD.getType());
                return dto;
            }
        }else {
            FreightChargeDto dto = new FreightChargeDto();
            dto.setErrorMsg(uploadResult.getErrorMsg());
            dto.setCourierCompanyCode(CourierCompanyEnum.COMPANY_JD.getType());
            return dto;
        }
        return null;
    }

    @Override
    public CourierCompanyEnum getCourierCompany() {
        return CourierCompanyEnum.COMPANY_JD;
    }
}
