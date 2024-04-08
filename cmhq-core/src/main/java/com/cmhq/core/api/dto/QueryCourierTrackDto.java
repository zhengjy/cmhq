package com.cmhq.core.api.dto;

import com.cmhq.core.api.UploadData;
import lombok.Data;

/**
 * Created by Jiyang.Zheng on 2024/4/8 10:55.
 */
@Data
public class QueryCourierTrackDto extends UploadData {
    String courierCompanyWaybillNo;

    @Override
    public String getUnKeyValue() {
        return courierCompanyWaybillNo;
    }
}
