package com.cmhq.core.util;

import com.alibaba.fastjson.JSONObject;
import com.cmhq.core.model.dto.FreightChargeDto;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

/**
 * Created by Jiyang.Zheng on 2024/4/23 14:03.
 */
@Slf4j
public class EstimatePriceUtil {
    /**
     * 不同订单公司运算不同
     * 首重费用+【实际重量（四舍五入取整）-1】*续重费用=实际费用
     * 实际重量【按实际重量与体积重量（计泡重量）两者取最大值计算运费】
     * 获取预估价格
     * @return
     */
    public static double getEstimatePrice(Integer retio,FreightChargeDto dto){
        try {
            if (dto != null && dto.getTotalPrice() != null){
                log.info("获取运费价格 {}", JSONObject.toJSONString(dto));
                BigDecimal b = new BigDecimal(dto.getTotalPrice() * ((double) retio /100));
                double estimatePrice = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                return estimatePrice;
            }
        }catch (Exception e){
            log.error("获取运费价格失败",e);
        }
        throw new RuntimeException("未查到运费价格");
    }
}
