package com.cmhq.core.fitler;

import com.cmhq.core.model.FaCourierOrderEntity;
import com.cmhq.core.model.dto.FreightChargeDto;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by Jiyang.Zheng on 2024/4/20 10:32.
 */
@Component
public abstract class AbstractFreightFilter  implements FreightFilter{

    @Override
    public FreightChargeDto getFreight(FaCourierOrderEntity order) {
        List<FreightFilter>  list = FitlerFactory.getFreightFilters();
        FreightChargeDto preDto = null;
        for (FreightFilter filter : list){
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
        return preDto;
    }

}
