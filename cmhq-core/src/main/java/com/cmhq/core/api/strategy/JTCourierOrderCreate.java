package com.cmhq.core.api.strategy;

import com.alibaba.fastjson.JSONObject;
import com.cmhq.core.api.UploadTypeEnum;
import com.cmhq.core.api.dto.JTCourierOrderDto;
import com.cmhq.core.api.dto.response.StoCreateCourierOrderResponseDto;
import com.cmhq.core.model.FaCourierOrderEntity;
import com.cmhq.core.model.dto.CreateCourierOrderResponseDto;
import org.springframework.stereotype.Component;

/**
 * Created by Jiyang.Zheng on 2024/4/7 13:56.
 */
@Component
public class JTCourierOrderCreate extends AbstractJTUpload<FaCourierOrderEntity, JTCourierOrderDto>{
    @Override
    public UploadTypeEnum supports() {
        return UploadTypeEnum.TYPE_JT_ORDER_UPLOAD;
    }

    @Override
    protected String getUploadUrl() {
        return "/api/order/addOrder";
    }

    @Override
    protected Object jsonMsgHandle(Object jsonMsg) {
        JSONObject dto = JSONObject.parseObject(JSONObject.toJSONString(jsonMsg), JSONObject.class);
        if (dto == null ){
            return null;
        }
        CreateCourierOrderResponseDto rsp = new CreateCourierOrderResponseDto();
        rsp.setWaybillNo(dto.getString("billCode"));
        rsp.setOrderNo(dto.getString("txlogisticId"));
        return rsp;
    }

    @Override
    protected JTCourierOrderDto getData(FaCourierOrderEntity param) throws RuntimeException {
        JSONObject map = JSONObject.parseObject(param.getCourierOrderExtend());
        if (map == null){
            map = new JSONObject();
        }
        JTCourierOrderDto dto = new JTCourierOrderDto();

        dto.setTxlogisticId(param.getOrderNo());

        JTCourierOrderDto.Sender sender = new JTCourierOrderDto.Sender();
        dto.setSender(sender);
        sender.setName(param.getFromName());
        sender.setMobile(param.getFromMobile());
        sender.setProv(param.getFromProv());
        sender.setCity(param.getFromCity());
        sender.setArea(param.getFromArea());
        sender.setAddress(param.getFromAddress());

        JTCourierOrderDto.Receiver receiver = new JTCourierOrderDto.Receiver();
        dto.setReceiver(receiver);
        receiver.setName(param.getToName());
        receiver.setMobile(param.getToMobile());
        receiver.setProv(param.getToProv());
        receiver.setCity(param.getToCity());
        receiver.setArea(param.getToArea());
        receiver.setAddress(param.getToAddress());

        dto.setSendStartTime(param.getTakeGoodsTime());
        dto.setSendEndTime(param.getTakeGoodsTime());

        dto.setGoodsType((String) map.get("jtGoodsType"));

        if (param.getLength() != null){
            dto.setLength(param.getLength().doubleValue());
        }
        if (param.getWidth() != null){
            dto.setWidth(param.getWidth().doubleValue());
        }
        if (param.getHeight() != null){
            dto.setHeight(param.getHeight().doubleValue());
        }
        if (param.getWeight() != null){
            dto.setWeight(param.getWeight());
        }
        return dto;
    }

}


