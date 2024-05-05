package com.cmhq.core.api.dto;

import com.cmhq.core.api.strategy.JTUploadData;
import lombok.Data;

/**
 * 取消订单
 * Created by Jiyang.Zheng on 2024/4/7 17:43.
 */
@Data
public class JTCancelCourierOrderDto extends JTUploadData {

    private String orderType ="2";
    private String txlogisticId;
    private String reason;

    @Override
    public String getUnKeyValue() {
        return txlogisticId;
    }
}
