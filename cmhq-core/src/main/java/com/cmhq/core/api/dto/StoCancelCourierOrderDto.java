package com.cmhq.core.api.dto;

import com.cmhq.core.api.UploadData;
import lombok.Data;

/**
 * 取消订单
 * Created by Jiyang.Zheng on 2024/4/7 17:43.
 */
@Data
public class StoCancelCourierOrderDto  extends UploadData {

    private String sourceOrderId;
    private String billCode;
    private String orderSource;
    private String remark;
    private String creater;
    private String orderType="01";

    @Override
    public String getUnKeyValue() {
        return billCode;
    }
}
