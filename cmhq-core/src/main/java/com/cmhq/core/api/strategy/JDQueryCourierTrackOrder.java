package com.cmhq.core.api.strategy;

import com.alibaba.fastjson.JSONObject;
import com.cmhq.core.api.UploadTypeEnum;
import com.cmhq.core.api.dto.response.JDCourierTrackRspDto;
import com.google.common.collect.Maps;
import com.lop.open.api.sdk.DefaultDomainApiClient;
import com.lop.open.api.sdk.domain.ECAP.CommonQueryOrderApi.commonGetOrderTraceV1.CommonOrderTraceDetail;
import com.lop.open.api.sdk.domain.ECAP.CommonQueryOrderApi.commonGetOrderTraceV1.CommonOrderTraceRequest;
import com.lop.open.api.sdk.domain.ECAP.CommonQueryOrderApi.commonGetOrderTraceV1.CommonOrderTraceResponse;
import com.lop.open.api.sdk.plugin.LopPlugin;
import com.lop.open.api.sdk.request.ECAP.EcapV1OrdersTraceQueryLopRequest;
import com.lop.open.api.sdk.response.ECAP.EcapV1OrdersTraceQueryLopResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.lop.open.api.sdk.domain.ECAP.CommonQueryOrderApi.commonGetOrderTraceV1.Response;


/**
 * Created by Jiyang.Zheng on 2024/4/7 13:56.
 */
@Slf4j
@Component
public class JDQueryCourierTrackOrder extends AbstractJDUpload<String, JDUploadData<CommonOrderTraceRequest>>{
    @Override
    public UploadTypeEnum supports() {
        return UploadTypeEnum.TYPE_JD_COURIER_QUERY_TRACK;
    }


    @Override
    protected JDUploadData<CommonOrderTraceRequest> getData(String courierCompanyWaybillNo) throws RuntimeException {
        CommonOrderTraceRequest dto = new CommonOrderTraceRequest();
        dto.setWaybillCode(courierCompanyWaybillNo);
        dto.setOrderOrigin(1);
        JDUploadData<CommonOrderTraceRequest> uploadData = new JDUploadData<>();
        uploadData.setUnKey2(courierCompanyWaybillNo);
        uploadData.setRequest(dto);
        return uploadData;
    }

    @Override
    protected Object doSend(JDUploadData<CommonOrderTraceRequest> uploadData) throws Exception {
        DefaultDomainApiClient client = geDefaultDomainApiClient();
        //入参对象（请记得更换为自己要使用的接口入参对象）
        EcapV1OrdersTraceQueryLopRequest request = new EcapV1OrdersTraceQueryLopRequest();
        request.setCommonOrderTraceRequest(uploadData.getRequest());
        LopPlugin lopPlugin = getProduceLopPlugin();
        request.addLopPlugin(lopPlugin);
        EcapV1OrdersTraceQueryLopResponse rsp  = client.execute(request);
        log.info("EcapV1OrdersTraceQueryLopResponse = {}",JSONObject.toJSONString(rsp));
        JDCourierTrackRspDto dto = JSONObject.parseObject(rsp.getMsg(),JDCourierTrackRspDto.class);
        return dto.getResponse().getContent();
    }

    @Override
    protected Object jsonMsgHandle(Object jsonMsg) {
        if (jsonMsg == null){
            return jsonMsg;
        }
        JDCourierTrackRspDto.Data resp = JSONObject.parseObject(JSONObject.toJSONString(jsonMsg), JDCourierTrackRspDto.Data.class);
        Map<String,String> map = Maps.newLinkedHashMap();
        if (resp == null || CollectionUtils.isEmpty(resp.getTraceDetails())){
            return jsonMsg;
        }
        List<JDCourierTrackRspDto.TraceDetails> detailList = resp.getTraceDetails().stream()
                .sorted(Comparator.comparing(JDCourierTrackRspDto.TraceDetails::getOperationTime).reversed()).collect(Collectors.toList());
        for (JDCourierTrackRspDto.TraceDetails detail : detailList){
            StringBuilder sb = new StringBuilder();
            sb.append("【");
            sb.append(detail.getOperationTitle());
            sb.append("】");
            sb.append("-");
            if (StringUtils.isNotEmpty(detail.getOperatorPhone()) && (detail.getCategory()==390 ||  detail.getCategory()== 420)){
                sb.append("联系电话:"+detail.getOperatorPhone()+"-");
            }
            sb.append(detail.getOperationRemark());
            sb.append("-");
            sb.append("【"+detail.getCategoryName()+"】");
            map.put(detail.getOperationTime(),sb.toString());
        }
        return map;
    }

}
