package com.cmhq.core.api.strategy;

import com.alibaba.fastjson.JSONObject;
import com.cmhq.core.api.UploadTypeEnum;
import com.lop.open.api.sdk.DefaultDomainApiClient;
import com.lop.open.api.sdk.domain.ECAP.CommonQueryOrderApi.commonGetOrderInfoV1.CommonOrderInfoRequest;
import com.lop.open.api.sdk.plugin.LopPlugin;
import com.lop.open.api.sdk.request.ECAP.EcapV1OrdersDetailsQueryLopRequest;
import com.lop.open.api.sdk.response.ECAP.EcapV1OrdersDetailsQueryLopResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


/**
 * Created by Jiyang.Zheng on 2024/4/7 13:56.
 */
@Slf4j
@Component
public class JDQueryOrderInfoOrder extends AbstractJDUpload<String, JDUploadData<CommonOrderInfoRequest>>{
    @Override
    public UploadTypeEnum supports() {
        return UploadTypeEnum.TYPE_JD_QUERY_COURIER_ORDER_INFO;
    }

    @Override
    protected Object doSend(JDUploadData<CommonOrderInfoRequest> uploadData) throws Exception {
        CommonOrderInfoRequest dto = uploadData.getRequest();
        DefaultDomainApiClient client = geDefaultDomainApiClient();
        EcapV1OrdersDetailsQueryLopRequest request = new EcapV1OrdersDetailsQueryLopRequest();
        request.setRequest(dto);
        LopPlugin lopPlugin = getProduceLopPlugin();
        request.addLopPlugin(lopPlugin);
        EcapV1OrdersDetailsQueryLopResponse rsp = client.execute(request);
        log.info("EcapV1OrdersDetailsQueryLopResponse = {}", JSONObject.toJSONString(rsp));
        return rsp.getResult();
    }


    @Override
    protected JDUploadData<CommonOrderInfoRequest> getData(String param) throws RuntimeException {
        CommonOrderInfoRequest dto = new CommonOrderInfoRequest();
        dto.setWaybillCode(param);
        dto.setCustomerCode(getCustomerCode());
        dto.setOrderOrigin(1);
        JDUploadData<CommonOrderInfoRequest> uploadData = new JDUploadData<>();
        uploadData.setUnKey2(param);
        uploadData.setRequest(dto);
        return uploadData;
    }
}