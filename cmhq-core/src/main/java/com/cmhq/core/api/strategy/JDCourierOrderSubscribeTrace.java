package com.cmhq.core.api.strategy;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cmhq.core.api.UploadTypeEnum;
import com.cmhq.core.api.dto.request.CourierOrderSubscribeTraceDto;
import com.cmhq.core.dao.FaCourierOrderDao;
import com.cmhq.core.model.FaCourierOrderEntity;
import com.cmhq.core.service.FaCourierOrderService;
import com.cmhq.core.util.SpringApplicationUtils;
import com.lop.open.api.sdk.DefaultDomainApiClient;
import com.lop.open.api.sdk.domain.ECAP.CommonSubscribeTraceApi.commonSubscribeTraceV1.CommonSubscribeTraceRequest;
import com.lop.open.api.sdk.plugin.LopPlugin;
import com.lop.open.api.sdk.request.ECAP.EcapV1OrdersTraceSubscribeLopRequest;
import com.lop.open.api.sdk.response.ECAP.EcapV1OrdersTraceSubscribeLopResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Created by Jiyang.Zheng on 2024/4/7 13:56.
 */
@Slf4j
@Component
public class JDCourierOrderSubscribeTrace extends AbstractJDUpload<CourierOrderSubscribeTraceDto, JDUploadData<CommonSubscribeTraceRequest>>{
    @Override
    public UploadTypeEnum supports() {
        return UploadTypeEnum.TYPE_JD_ORDER_SUBSCRIBE_TRACE;
    }

    @Override
    protected Object doSend(JDUploadData<CommonSubscribeTraceRequest> uploadData) throws Exception {
        CommonSubscribeTraceRequest dto = uploadData.getRequest();
        DefaultDomainApiClient client = geDefaultDomainApiClient();
        EcapV1OrdersTraceSubscribeLopRequest request = new EcapV1OrdersTraceSubscribeLopRequest();
        request.setRequest(dto);
        LopPlugin lopPlugin = getProduceLopPlugin();
        request.addLopPlugin(lopPlugin);
        EcapV1OrdersTraceSubscribeLopResponse rsp = client.execute(request);
        log.info("EcapV1OrdersTraceSubscribeLopResponse = {}",JSONObject.toJSONString(rsp));
        return rsp.getResult();
    }


    @Override
    protected JDUploadData<CommonSubscribeTraceRequest> getData(CourierOrderSubscribeTraceDto param) throws RuntimeException {

        CommonSubscribeTraceRequest dto = new CommonSubscribeTraceRequest();
        dto.setWaybillCode(param.getWaybillCode());

        FaCourierOrderDao faCourierOrderDao = SpringApplicationUtils.getBean(FaCourierOrderDao.class);
        FaCourierOrderEntity order = faCourierOrderDao.selectOne(new LambdaQueryWrapper<FaCourierOrderEntity>().eq(FaCourierOrderEntity::getCourierCompanyWaybillNo,param.getWaybillCode()));
        dto.setOrderOrigin(order.getOrderOrigin());
        dto.setCustomerCode(getCustomerCode(order.getOrderOrigin()));
        dto.setMobile(param.getMobile().substring(7));

        JDUploadData<CommonSubscribeTraceRequest> uploadData = new JDUploadData<>();

        uploadData.setUnKey2(param.getWaybillCode());
        uploadData.setRequest(dto);
        return uploadData;
    }

}


