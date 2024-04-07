package com.cmhq.core.api.strategy;

import com.alibaba.fastjson.JSONObject;
import com.cmhq.core.api.UploadTypeEnum;
import com.cmhq.core.api.dto.StoCourierOrderDto;
import com.cmhq.core.model.FaCourierOrderEntity;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Jiyang.Zheng on 2024/4/7 13:56.
 */
@Component
public class StoCourierOrderCreate extends AbstractStoUpload<FaCourierOrderEntity, StoCourierOrderDto>{
    @Override
    public UploadTypeEnum supports() {
        return UploadTypeEnum.TYPE_STO_ORDER_UPLOAD;
    }

    @Override
    protected String getUploadUrl() {
        return "EDI_PUSH_ORDER_STATUS";
    }

    @Override
    protected void syncTransferStatus(List<String> code, String status) {

    }

    @Override
    protected List<StoCourierOrderDto> getData(FaCourierOrderEntity param) throws RuntimeException {
        JSONObject map = JSONObject.parseObject(param.getCourierOrderExtend());
        if (map == null){
            map = new JSONObject();
        }
        StoCourierOrderDto dto = new StoCourierOrderDto();
        dto.setOrderNo(param.getOrderNo());
        dto.setOrderSource("");//TODO
        dto.setBillType((String) map.get("billType"));
        dto.setOrderType("01");

        StoCourierOrderDto.Sender sender = new StoCourierOrderDto.Sender();
        dto.setSender(sender);
        sender.setName(param.getFromName());
        sender.setMobile(param.getFromMobile());
        sender.setProvince(param.getFromProv());
        sender.setCity(param.getFromCity());
        sender.setArea(param.getFromArea());
        sender.setAddress(param.getFromAddress());

        StoCourierOrderDto.Receiver receiver = new StoCourierOrderDto.Receiver();
        dto.setReceiver(receiver);
        receiver.setName(param.getToName());
        receiver.setMobile(param.getToMobile());
        receiver.setProvince(param.getToProv());
        receiver.setCity(param.getToCity());
        receiver.setArea(param.getToArea());
        receiver.setAddress(param.getToAddress());

        StoCourierOrderDto.Cargo cargo = new StoCourierOrderDto.Cargo();
        dto.setCargo(cargo);
        cargo.setBattery((String) map.get("battery"));
        cargo.setGoodsType(param.getGoodsType());
        cargo.setGoodsName(param.getGoodsName());
        if (param.getLength() != null){
            cargo.setSpaceX(param.getLength().doubleValue());
        }
        if (param.getWidth() != null){
            cargo.setSpaceY(param.getWidth().doubleValue());
        }
        if (param.getHeight() != null){
            cargo.setSpaceZ(param.getHeight().doubleValue());
        }
        if (param.getWeight() != null){
            cargo.setWeight(param.getWeight());
        }
        return Arrays.asList(dto);
    }
}
