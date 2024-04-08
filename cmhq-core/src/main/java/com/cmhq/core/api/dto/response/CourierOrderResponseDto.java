package com.cmhq.core.api.dto.response;

import lombok.Data;

/**
 * Created by Jiyang.Zheng on 2024/4/8 14:54.
 */
@Data
public class CourierOrderResponseDto {
    /**
     * {
     *     "orderNo": "88875485332",
     *     "waybillNo": "47893154",
     *     "bigWord": "877-342-213",
     *     "packagePlace": "上海青浦测试集包地",
     *     "sourceOrderId": "88875485332",
     *     "safeNo": "95013740109424"
     *   }
     */
    /** */
    private String orderNo;
    /** */
    private String waybillNo;

}
