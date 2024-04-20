package com.cmhq.core.api.dto;

import lombok.Data;

import java.util.List;

/**
 * Created by Jiyang.Zheng on 2024/4/8 10:55.
 */
@Data
public class JTQueryCourierTrackDto extends JTUploadData {
    /**排序方式*/
    List<String> billCodes;


    @Override
    public String getUnKeyValue() {
        return billCodes.get(0);
    }
}
