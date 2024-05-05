package com.cmhq.core.api.strategy;

import com.alibaba.fastjson.JSONObject;
import com.cmhq.core.api.UploadTypeEnum;
import com.cmhq.core.model.FaCourierOrderEntity;
import com.lop.open.api.sdk.DefaultDomainApiClient;
import com.lop.open.api.sdk.domain.ECAP.CommonQueryOrderApi.commonGetOrderStatusV1.CommonOrderStatusRequest;
import com.lop.open.api.sdk.domain.ECAP.CommonQueryOrderApi.commonGetOrderStatusV1.CommonOrderStatusResponse;
import com.lop.open.api.sdk.domain.ECAP.CommonQueryOrderApi.commonGetOrderStatusV1.Response;
import com.lop.open.api.sdk.plugin.LopPlugin;
import com.lop.open.api.sdk.request.ECAP.EcapV1OrdersStatusGetLopRequest;
import com.lop.open.api.sdk.response.ECAP.EcapV1OrdersStatusGetLopResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


/**
 * 查询订单状态
 * Created by Jiyang.Zheng on 2024/4/7 13:56.
 */
@Slf4j
@Component
public class JDQueryCourierOrderStatus extends AbstractJDUpload<FaCourierOrderEntity, JDUploadData<CommonOrderStatusRequest>>{
    @Override
    public UploadTypeEnum supports() {
        return UploadTypeEnum.TYPE_JD_QUERY_COURIER_ORDER_STATUS;
    }

    @Override
    protected Response<CommonOrderStatusResponse> doSend(JDUploadData<CommonOrderStatusRequest> uploadData) throws Exception {
        CommonOrderStatusRequest dto = uploadData.getRequest();
        DefaultDomainApiClient client = geDefaultDomainApiClient();
        EcapV1OrdersStatusGetLopRequest request = new EcapV1OrdersStatusGetLopRequest();
        request.setRequest(dto);
        LopPlugin lopPlugin = getProduceLopPlugin();
        request.addLopPlugin(lopPlugin);
        EcapV1OrdersStatusGetLopResponse rsp = client.execute(request);
        log.info("EcapV1OrdersStatusGetLopResponse = {}", JSONObject.toJSONString(rsp));
        return rsp.getResult();
    }


    @Override
    protected JDUploadData<CommonOrderStatusRequest> getData(FaCourierOrderEntity param) throws RuntimeException {
        CommonOrderStatusRequest dto = new CommonOrderStatusRequest();
        dto.setWaybillCode(param.getCourierCompanyWaybillNo());
        dto.setOrderCode(param.getCourierCompanyOrderNo());
        dto.setCustomerCode(getCustomerCode());
        dto.setOrderOrigin(1);
        JDUploadData<CommonOrderStatusRequest> uploadData = new JDUploadData<>();
        uploadData.setUnKey2(param.getOrderNo());
        uploadData.setRequest(dto);
        return uploadData;
    }

}
