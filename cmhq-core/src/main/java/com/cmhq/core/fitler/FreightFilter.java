package com.cmhq.core.fitler;

import com.cmhq.core.model.FaCourierOrderEntity;
import com.cmhq.core.model.dto.FreightChargeDto;

/**
 * 费率计算
 * Created by Jiyang.Zheng on 2024/4/20 10:25.
 */
public interface FreightFilter {

    /**
     * 查找最低订单运费
     * @param order
     * @return
     */
    FreightChargeDto getFreight(FaCourierOrderEntity order);

    FreightChargeDto doFreightHandle(FaCourierOrderEntity order);
}
