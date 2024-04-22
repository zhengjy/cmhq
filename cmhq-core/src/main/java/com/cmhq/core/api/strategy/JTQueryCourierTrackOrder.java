package com.cmhq.core.api.strategy;

import com.alibaba.fastjson.JSONObject;
import com.cmhq.core.api.UploadTypeEnum;
import com.cmhq.core.api.dto.JTQueryCourierTrackDto;
import com.cmhq.core.api.dto.request.JTPushTraceDto;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


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
    protected Object jsonMsgHandle(Object jsonMsg) {
        if (jsonMsg == null){
            return jsonMsg;
        }
        JTPushTraceDto dto = JSONObject.parseObject(JSONObject.toJSONString(jsonMsg),JTPushTraceDto.class);
        if (dto == null || dto.getDetails() == null){
            return jsonMsg;
        }
        Map<String,String> map = Maps.newLinkedHashMap();
        List<JTPushTraceDto.Detail> detailList = dto.getDetails().stream().sorted(Comparator.comparing(JTPushTraceDto.Detail::getScanTime)).collect(Collectors.toList());
        for (JTPushTraceDto.Detail detail : detailList){
            map.put(detail.getScanTime(),detail.getDesc());

        }
        return map;
    }

}
