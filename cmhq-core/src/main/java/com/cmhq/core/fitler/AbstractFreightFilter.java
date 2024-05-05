package com.cmhq.core.fitler;

import com.cmhq.core.model.FaCourierOrderEntity;
import com.cmhq.core.model.dto.FreightChargeDto;
import com.cmhq.core.util.EstimatePriceUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by Jiyang.Zheng on 2024/4/20 10:32.
 */
@Component
public abstract class AbstractFreightFilter  implements FreightFilter{

    @Override
    public FreightChargeDto getFreight(FaCourierOrderEntity order) {
        return doGetFreight(order,null);
    }

    @Override
    public FreightChargeDto getFreightByCourierCompanyCode(FaCourierOrderEntity order,String courierCompanyCode) {
        return doGetFreight(order,courierCompanyCode);
    }

    private FreightChargeDto doGetFreight(FaCourierOrderEntity order,String courierCompanyCode){
        FreightChargeDto dto =  EstimatePriceUtil.getCourerCompanyCostPrice(order.getFromProv(),order.getToProv(),order.getFromCity(),order.getToCity(),order.getWeight(),null);
        if (StringUtils.isEmpty(courierCompanyCode)){
            courierCompanyCode = dto.getCourierCompanyCode();
        }
        List<FreightFilter>  list = FitlerFactory.getFreightFilters();
        FreightChargeDto preDto = null;
        for (FreightFilter filter : list){
            if (StringUtils.isNotEmpty(courierCompanyCode)){
                if (filter.getCourierCompany().getType().equals(courierCompanyCode)){
                    FreightChargeDto currDto = filter.doFreightHandle(order);
                    if (preDto == null){
                        preDto = currDto;
                    }else {
                        if (currDto != null && preDto != null){
                            if (currDto.getTotalPrice() < preDto.getTotalPrice()){
                                preDto =  currDto;
                            }
                        }
                    }
                }
            }else {
                FreightChargeDto currDto = filter.doFreightHandle(order);
                if (preDto == null){
                    preDto = currDto;
                }else {
                    if (currDto != null && preDto != null){
                        if (currDto.getTotalPrice() < preDto.getTotalPrice()){
                            preDto =  currDto;
                        }
                    }
                }
            }
        }
        return preDto;
    }
}
