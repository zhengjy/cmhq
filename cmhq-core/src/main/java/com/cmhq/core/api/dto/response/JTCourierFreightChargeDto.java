package com.cmhq.core.api.dto.response;

import lombok.Data;

import java.util.List;

/**
 * Created by Jiyang.Zheng on 2024/4/13 22:05.
 */
@Data
public class JTCourierFreightChargeDto {
    /**运费总价（单位：元），保留两位小数*/
    private Double totalPrice;

}