package com.cmhq.core.api.strategy;

import com.alibaba.fastjson.JSONObject;
import com.cmhq.core.api.UploadTypeEnum;
import com.cmhq.core.api.dto.QueryCourierTrackDto;
import com.cmhq.core.api.dto.response.StoCourierTrackRspDto;
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
        dto.setWaybillNoList(Arrays.asList(new QueryCourierTrackDto.BillOrderNo(courierCompanyWaybillNo)));
        return dto;
    }

    @Override
    protected String getToCode() {
        return "sto_trace_query";
    }

    @Override
    protected Object jsonMsgHandle(Object jsonMsg) {
        if (jsonMsg == null){
            return null;
        }
        StoCourierTrackRspDto dto = JSONObject.parseObject(JSONObject.toJSONString(jsonMsg),StoCourierTrackRspDto.class);
        if (dto == null || dto.getWaybillNo() == null){
            return null;
        }
        Map<String,String> map = Maps.newLinkedHashMap();
        List<StoCourierTrackRspDto.WaybillInfo> detailList = dto.getWaybillNo().stream().sorted(Comparator.comparing(StoCourierTrackRspDto.WaybillInfo::getOpTime)).collect(Collectors.toList());
        for (StoCourierTrackRspDto.WaybillInfo detail : detailList){
            map.put(detail.getOpTime(),detail.getMemo());
        }
        return map;
    }
}
