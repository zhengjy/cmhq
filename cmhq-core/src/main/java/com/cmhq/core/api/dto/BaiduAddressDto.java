package com.cmhq.core.api.dto;

import com.cmhq.core.api.UploadData;
import lombok.Data;

/**
 * Created by Jiyang.Zheng on 2024/4/7 20:53.
 */
@Data
public class BaiduAddressDto extends UploadData {

    private String text;

    @Override
    public String getUnKeyValue() {
        return "1";
    }
}
