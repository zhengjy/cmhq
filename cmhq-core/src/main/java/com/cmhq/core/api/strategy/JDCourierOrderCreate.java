package com.cmhq.core.api.strategy;

import com.alibaba.fastjson.JSONObject;
import com.cmhq.core.api.UploadTypeEnum;
import com.cmhq.core.model.FaCourierOrderEntity;
import com.cmhq.core.model.dto.CreateCourierOrderResponseDto;
import com.google.common.collect.Maps;
import com.lop.open.api.sdk.DefaultDomainApiClient;
import com.lop.open.api.sdk.domain.ECAP.CommonCreateOrderApi.commonCreateOrderV1.CommonCargoInfo;
import com.lop.open.api.sdk.domain.ECAP.CommonCreateOrderApi.commonCreateOrderV1.CommonCreateOrderRequest;
import com.lop.open.api.sdk.domain.ECAP.CommonCreateOrderApi.commonCreateOrderV1.CommonCreateOrderResponse;
import com.lop.open.api.sdk.domain.ECAP.CommonCreateOrderApi.commonCreateOrderV1.Response;
import com.lop.open.api.sdk.plugin.LopPlugin;
import com.lop.open.api.sdk.request.ECAP.EcapV1OrdersCreateLopRequest;
import com.lop.open.api.sdk.request.ECAP.EcapV1OrdersPrecheckLopRequest;
import com.lop.open.api.sdk.response.ECAP.EcapV1OrdersCreateLopResponse;
import com.lop.open.api.sdk.response.ECAP.EcapV1OrdersPrecheckLopResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Jiyang.Zheng on 2024/4/7 13:56.
 */
@Slf4j
@Component
public class JDCourierOrderCreate extends AbstractJDUpload<FaCourierOrderEntity, JDUploadData<CommonCreateOrderRequest>>{
    @Override
    public UploadTypeEnum supports() {
        return UploadTypeEnum.TYPE_JD_ORDER_UPLOAD;
    }

    @Override
    protected Object doSend(JDUploadData<CommonCreateOrderRequest> uploadData) throws Exception {
        CommonCreateOrderRequest dto = uploadData.getRequest();
        //下单前置校验
//        doCheck(uploadData.getRequest());
        DefaultDomainApiClient client = geDefaultDomainApiClient();
        EcapV1OrdersCreateLopRequest request = new EcapV1OrdersCreateLopRequest();
        request.setRequest(dto);
        LopPlugin lopPlugin = getProduceLopPlugin();
        request.addLopPlugin(lopPlugin);
        EcapV1OrdersCreateLopResponse rsp = client.execute(request);
        log.info("EcapV1OrdersCreateLopResponse = {}",JSONObject.toJSONString(rsp));
        return rsp.getResult();
    }

    private void doCheck(CommonCreateOrderRequest param) throws Exception{
        DefaultDomainApiClient client = geDefaultDomainApiClient();
        //入参对象（请记得更换为自己要使用的接口入参对象）
        EcapV1OrdersPrecheckLopRequest request = new EcapV1OrdersPrecheckLopRequest();
        com.lop.open.api.sdk.domain.ECAP.CommonCreateOrderApi.commonCheckPreCreateOrderV1.CommonCreateOrderRequest dto = JSONObject.parseObject(JSONObject.toJSONString(param),com.lop.open.api.sdk.domain.ECAP.CommonCreateOrderApi.commonCheckPreCreateOrderV1.CommonCreateOrderRequest.class);
        request.setRequest(dto);
        LopPlugin lopPlugin = getProduceLopPlugin();
        request.addLopPlugin(lopPlugin);
        EcapV1OrdersPrecheckLopResponse rsp  = client.execute(request);
        log.info("EcapV1OrdersPrecheckLopResponse = {}",JSONObject.toJSONString(rsp));
        if (!isSuccess(rsp.getCode()) && (rsp.getResult() == null ||  !rsp.getResult().getSuccess())){
            throw new RuntimeException(rsp.getMsg());
        }
    }

    @Override
    protected Object jsonMsgHandle(Object jsonMsg) {
        CommonCreateOrderResponse response = JSONObject.parseObject(JSONObject.toJSONString(jsonMsg), CommonCreateOrderResponse.class);
        CreateCourierOrderResponseDto rsp = new CreateCourierOrderResponseDto();
        rsp.setWaybillNo(response.getWaybillCode());
        rsp.setOrderNo(response.getOrderCode());
        return rsp;
    }

    @Override
    protected JDUploadData<CommonCreateOrderRequest> getData(FaCourierOrderEntity param) throws RuntimeException {
        JSONObject map = JSONObject.parseObject(param.getCourierOrderExtend());
        if (map == null){
            map = new JSONObject();
        }

        CommonCreateOrderRequest dto = new CommonCreateOrderRequest();
        dto.setOrderId(param.getOrderNo());

        com.lop.open.api.sdk.domain.ECAP.CommonCreateOrderApi.commonCreateOrderV1.Contact sender = new com.lop.open.api.sdk.domain.ECAP.CommonCreateOrderApi.commonCreateOrderV1.Contact();
        dto.setSenderContact(sender);
        sender.setName(param.getFromName());
        sender.setMobile(param.getFromMobile());
        sender.setFullAddress(param.getFromProv()+param.getFromCity()+param.getFromArea()+param.getFromAddress());

        com.lop.open.api.sdk.domain.ECAP.CommonCreateOrderApi.commonCreateOrderV1.Contact  receiver = new com.lop.open.api.sdk.domain.ECAP.CommonCreateOrderApi.commonCreateOrderV1.Contact ();
        dto.setReceiverContact(receiver);
        receiver.setName(param.getToName());
        receiver.setMobile(param.getToMobile());
        receiver.setFullAddress(param.getToProv()+param.getToCity()+param.getToArea()+param.getToAddress());

        dto.setOrderOrigin(1); //TODO 下单来源
        dto.setCustomerCode(getCustomerCode());//TODO 问客户编码

        com.lop.open.api.sdk.domain.ECAP.CommonCreateOrderApi.commonCreateOrderV1.CommonProductInfo product = new com.lop.open.api.sdk.domain.ECAP.CommonCreateOrderApi.commonCreateOrderV1.CommonProductInfo();
        dto.setProductsReq(product);
        product.setProductCode("ed-m-0001");//TODO 问签约的哪一种

        dto.setSettleType(3); //TODO  付款方式

        List<CommonCargoInfo> cargoes = new ArrayList<>();
        CommonCargoInfo commonCargoInfo = new CommonCargoInfo();
        commonCargoInfo.setName(param.getGoodsName());
        commonCargoInfo.setQuantity(1);
        commonCargoInfo.setWeight(BigDecimal.valueOf(param.getWeight()));
        commonCargoInfo.setVolume(BigDecimal.valueOf(1));//TODO 问是否可以不填
        if (param.getLength() != null){
            commonCargoInfo.setLength(BigDecimal.valueOf(param.getLength().doubleValue()));
        }
        if (param.getWidth() != null){
            commonCargoInfo.setWidth(BigDecimal.valueOf(param.getWidth().doubleValue()));
        }
        if (param.getHeight() != null){
            commonCargoInfo.setHight(BigDecimal.valueOf(param.getHeight().doubleValue()));
        }
        cargoes.add(commonCargoInfo);
        dto.setCargoes(cargoes);
        String s1 = (StringUtils.isNotEmpty(param.getFromPartitionNumber()) ? param.getFromPartitionNumber() : "") ;
        String s2 = (StringUtils.isNotEmpty(param.getToPartitionNumber()) ? param.getToPartitionNumber() : "") ;
        String s3 = (StringUtils.isNotEmpty(param.getMsg()) ? param.getMsg() : "") ;
        dto.setRemark("发货分区号："+s1 +"；收货分区号："+s2 +"；" +s3);
        //下单即订阅 1-物流轨迹
        Map<String,String> extendProps = Maps.newHashMap();
        extendProps.put("autoSubscribe","1");
        dto.setExtendProps(extendProps);

        if (StringUtils.isNotEmpty(param.getTakeGoodsTimeEnd())){
            SimpleDateFormat ft2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                dto.setPickupStartTime(ft2.parse(param.getTakeGoodsTime()));
                dto.setPickupEndTime(ft2.parse(param.getTakeGoodsTimeEnd()));
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }else if (StringUtils.isNotEmpty(param.getTakeGoodsTime())){
            SimpleDateFormat ft2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                Date date = ft2.parse(param.getTakeGoodsTime());
                date.setTime(date.getTime() - 1000* 60);
                dto.setPickupStartTime(date);
                dto.setPickupEndTime(ft2.parse(param.getTakeGoodsTime()));
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }

        JDUploadData<CommonCreateOrderRequest> uploadData = new JDUploadData<>();

        uploadData.setUnKey2(param.getOrderNo());
        uploadData.setRequest(dto);
        return uploadData;
    }

}


