package com.cmhq.core.api.strategy;

import com.cmhq.core.api.UploadTypeEnum;
import com.cmhq.core.api.dto.JTQueryCourierTrackDto;
import org.springframework.stereotype.Component;

import java.util.Arrays;


/**
 * Created by Jiyang.Zheng on 2024/4/7 13:56.
 */
@Component
public class JTQueryCourierTrackOrder extends AbstractJTUpload<String, JTQueryCourierTrackDto>{
    @Override
    public UploadTypeEnum supports() {
        return UploadTypeEnum.TYPE_JT_COURIER_QUERY_TRACK;
    }

    @Override
    protected String getUploadUrl() {
        return "/api/logistics/trace";
    }

    @Override
    protected JTQueryCourierTrackDto getData(String courierCompanyWaybillNo) throws RuntimeException {
        JTQueryCourierTrackDto dto = new JTQueryCourierTrackDto();
        dto.setBillCodes(Arrays.asList(courierCompanyWaybillNo));
        return dto;
    }

    @Override
    protected void setContentDigest(JTQueryCourierTrackDto uploadData) {
    }
}
