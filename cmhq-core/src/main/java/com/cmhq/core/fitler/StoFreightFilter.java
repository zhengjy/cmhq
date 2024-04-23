package com.cmhq.core.fitler;

import com.alibaba.fastjson.JSONObject;
import com.cmhq.core.api.UploadResult;
import com.cmhq.core.api.UploadTypeEnum;
import com.cmhq.core.api.dto.response.StoCourierFreightChargeDto;
import com.cmhq.core.api.strategy.StrategyFactory;
import com.cmhq.core.api.strategy.Upload;
import com.cmhq.core.enums.CourierCompanyEnum;
import com.cmhq.core.model.FaCourierOrderEntity;
import com.cmhq.core.model.dto.FreightChargeDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;



/**
 * Created by Jiyang.Zheng on 2024/4/20 10:29.
 */
@Slf4j
@Component
public class StoFreightFilter  extends AbstractFreightFilter{
    @Override
    public FreightChargeDto doFreightHandle(FaCourierOrderEntity order) {
        Upload upload = StrategyFactory.getUpload(UploadTypeEnum.TYPE_STO_QUERY_FREIGHT_CHARGE);
        UploadResult uploadResult = upload.execute(order);
        if (uploadResult.getFlag()){
            StoCourierFreightChargeDto ccd = JSONObject.parseObject(uploadResult.getJsonMsg()+"", StoCourierFreightChargeDto.class);
            if (uploadResult.getJsonMsg() == null || CollectionUtils.isEmpty(ccd.getAvailableServiceItemList()) || ccd.getAvailableServiceItemList().get(0).getFeeModel() == null){
            }else {
                StoCourierFreightChargeDto.FeeModel feeModel = ccd.getAvailableServiceItemList().get(0).getFeeModel();
                Integer totalPrice = Integer.parseInt(feeModel.getTotalPrice());
                log.info("获取sto运费价格 {}",JSONObject.toJSONString(feeModel));
                FreightChargeDto dto = new FreightChargeDto();
                //原始价格set
                dto.setTotalPrice((double)totalPrice  / 100);
                dto.setCourierCompanyCode(CourierCompanyEnum.COMPANY_STO.getType());
                return dto;
            }
        }
        return null;
    }

    @Override
    public CourierCompanyEnum getCourierCompany() {
        return CourierCompanyEnum.COMPANY_STO;
    }
}
