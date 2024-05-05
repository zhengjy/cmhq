package com.cmhq.core.api.strategy;

import com.alibaba.fastjson.JSONObject;
import com.cmhq.core.api.UploadTypeEnum;
import com.cmhq.core.model.FaCourierOrderEntity;
import com.lop.open.api.sdk.DefaultDomainApiClient;
import com.lop.open.api.sdk.domain.ECAP.CommonModifyCancelOrderApi.commonCancelOrderV1.CommonModifyCancelOrderResponse;
import com.lop.open.api.sdk.domain.ECAP.CommonModifyCancelOrderApi.commonCancelOrderV1.CommonOrderCancelRequest;
import com.lop.open.api.sdk.domain.ECAP.CommonModifyCancelOrderApi.commonCancelOrderV1.Response;
import com.lop.open.api.sdk.plugin.LopPlugin;
import com.lop.open.api.sdk.request.ECAP.EcapV1OrdersCancelLopRequest;
import com.lop.open.api.sdk.response.ECAP.EcapV1OrdersCancelLopResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;



/**
 * Created by Jiyang.Zheng on 2024/4/7 13:56.
 */
@Slf4j
@Component
public class JDCancelCourierOrder extends AbstractJDUpload<FaCourierOrderEntity, JDUploadData<CommonOrderCancelRequest>>{
    @Override
    public UploadTypeEnum supports() {
        return UploadTypeEnum.TYPE_JD_CANCEL_COURIER_ORDER;
    }

    @Override
    protected Response<CommonModifyCancelOrderResponse> doSend(JDUploadData<CommonOrderCancelRequest> uploadData) throws Exception {
        CommonOrderCancelRequest dto = uploadData.getRequest();
        //下单前置校验
        DefaultDomainApiClient client = geDefaultDomainApiClient();
        EcapV1OrdersCancelLopRequest request = new EcapV1OrdersCancelLopRequest();
        request.setRequest(dto);
        LopPlugin lopPlugin = getProduceLopPlugin();
        request.addLopPlugin(lopPlugin);
        EcapV1OrdersCancelLopResponse rsp = client.execute(request);
        log.info("EcapV1OrdersCancelLopResponse = {}", JSONObject.toJSONString(rsp));
        return rsp.getResult();
    }


    @Override
    protected JDUploadData<CommonOrderCancelRequest> getData(FaCourierOrderEntity param) throws RuntimeException {
        CommonOrderCancelRequest dto = new CommonOrderCancelRequest();
        dto.setWaybillCode(param.getCourierCompanyWaybillNo());
        dto.setOrderCode(param.getCourierCompanyOrderNo());
        dto.setCustomerCode(getCustomerCode());
        dto.setOrderOrigin(1);
        dto.setCancelReason(param.getReason());
        dto.setCancelReasonCode("1");
        JDUploadData<CommonOrderCancelRequest> uploadData = new JDUploadData<>();
        uploadData.setUnKey2(param.getOrderNo());
        uploadData.setRequest(dto);
        return uploadData;
    }

//    @Override
//    public void uploadResultHandle(JDUploadData<CommonOrderCancelRequest> uploadData, UploadResult uploadResult) {
//        Response resp = JSONObject.parseObject(JSONObject.toJSONString(uploadResult.getJsonMsg()), Response.class);
//        if (!resp.getSuccess()){
//            uploadResult.setFlag(false);
//        }
//        super.uploadResultHandle(uploadData,uploadResult);
//    }
}
