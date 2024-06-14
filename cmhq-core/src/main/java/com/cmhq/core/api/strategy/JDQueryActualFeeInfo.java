package com.cmhq.core.api.strategy;

import com.alibaba.fastjson.JSONObject;
import com.cmhq.core.api.UploadTypeEnum;
import com.cmhq.core.model.FaCourierOrderEntity;
import com.lop.open.api.sdk.DefaultDomainApiClient;
import com.lop.open.api.sdk.domain.ECAP.CommonCreateOrderApi.commonCheckPreCreateOrderV1.CommonCargoInfo;
import com.lop.open.api.sdk.domain.ECAP.CommonCreateOrderApi.commonCheckPreCreateOrderV1.CommonCreateOrderRequest;
import com.lop.open.api.sdk.domain.ECAP.CommonCreateOrderApi.commonCheckPreCreateOrderV1.CommonProductInfo;
import com.lop.open.api.sdk.domain.ECAP.CommonCreateOrderApi.commonCheckPreCreateOrderV1.Contact;
import com.lop.open.api.sdk.domain.ECAP.CommonQueryOrderApi.commonGetActualFeeInfoV1.CommonActualFeeRequest;
import com.lop.open.api.sdk.plugin.LopPlugin;
import com.lop.open.api.sdk.request.ECAP.EcapV1OrdersActualfeeQueryLopRequest;
import com.lop.open.api.sdk.request.ECAP.EcapV1OrdersPrecheckLopRequest;
import com.lop.open.api.sdk.response.ECAP.EcapV1OrdersActualfeeQueryLopResponse;
import com.lop.open.api.sdk.response.ECAP.EcapV1OrdersPrecheckLopResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


/**查询
 * Created by Jiyang.Zheng on 2024/4/7 13:56.
 */
@Slf4j
@Component
public class JDQueryActualFeeInfo extends AbstractJDUpload<String, JDUploadData<CommonActualFeeRequest>>{
    @Override
    public UploadTypeEnum supports() {
        return UploadTypeEnum.TYPE_JD_ORDER_ACTUAL_FEE;
    }


    @Override
    protected Object doSend(JDUploadData<CommonActualFeeRequest> uploadData) throws Exception {
        DefaultDomainApiClient client = geDefaultDomainApiClient();
        //入参对象（请记得更换为自己要使用的接口入参对象）
        EcapV1OrdersActualfeeQueryLopRequest request = new EcapV1OrdersActualfeeQueryLopRequest();
        request.setRequest(uploadData.getRequest());
        LopPlugin lopPlugin = getProduceLopPlugin();
        request.addLopPlugin(lopPlugin);
        EcapV1OrdersActualfeeQueryLopResponse rsp  = client.execute(request);
        log.info("EcapV1OrdersActualfeeQueryLopResponse = {}",JSONObject.toJSONString(rsp));
        return rsp.getResult();
    }


    @Override
    protected JDUploadData<CommonActualFeeRequest> getData(String waybillCode) throws RuntimeException {
        CommonActualFeeRequest dto = new CommonActualFeeRequest();

        dto.setOrderOrigin(1);
        dto.setCustomerCode(getCustomerCode());
        dto.setWaybillCode(waybillCode);
        JDUploadData<CommonActualFeeRequest> uploadData = new JDUploadData<>();
        uploadData.setUnKey2(waybillCode);
        uploadData.setRequest(dto);
        return uploadData;
    }

}
