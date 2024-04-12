package com.cmhq.core.api.strategy;

import com.cmhq.core.api.UploadTypeEnum;
import com.cmhq.core.api.dto.QueryCourierTrackDto;
import org.springframework.stereotype.Component;


/**
 * Created by Jiyang.Zheng on 2024/4/7 13:56.
 */
@Component
public class StoQueryCourierTrackOrder extends AbstractStoUpload<String, QueryCourierTrackDto>{
    @Override
    public UploadTypeEnum supports() {
        return UploadTypeEnum.TYPE_STO_COURIER_QUERY_TRACK;
    }

    @Override
    protected String getUploadUrl() {
        return "STO_TRACE_QUERY_COMMON";
    }

    @Override
    protected QueryCourierTrackDto getData(String courierCompanyWaybillNo) throws RuntimeException {
        QueryCourierTrackDto dto = new QueryCourierTrackDto();
        dto.setCourierCompanyWaybillNo(courierCompanyWaybillNo);
        return dto;
    }

    @Override
    protected String getToCode() {
        return "sto_trace_query";
    }
}
