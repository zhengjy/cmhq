package com.cmhq.core.api.dto;

import com.cmhq.core.api.UploadData;
import lombok.Data;

import java.util.List;

/**
 * Created by Jiyang.Zheng on 2024/4/8 10:55.
 */
@Data
public class QueryCourierTrackDto extends UploadData {
    /**排序方式*/
    String order ="asc";
    List<BillOrderNo> waybillNoList;


    @Data
    public static class BillOrderNo{
        private String waybillNo;

        public BillOrderNo(String waybillNo) {
            this.waybillNo = waybillNo;
        }
    }
    @Override
    public String getUnKeyValue() {
        return waybillNoList.get(0).waybillNo;
    }
}
