package com.cmhq.core.api.strategy;

import com.alibaba.fastjson.JSONObject;
import com.cmhq.core.api.UploadTypeEnum;
import com.cmhq.core.api.dto.JTCourierOrderDto;
import com.cmhq.core.model.FaCourierOrderEntity;
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
    protected JTCourierOrderDto getData(FaCourierOrderEntity param) throws RuntimeException {
        JSONObject map = JSONObject.parseObject(param.getCourierOrderExtend());
        if (map == null){
            map = new JSONObject();
        }
        String customerCode = "";
        String privateKey = "";
        String[] split = getToken().split(",");
        if (split.length >= 3) {
            privateKey = split[1];
            customerCode = split[2];
        }
        //业务digest 签名，Base64(Md5(客户编号+密文+privateKey))，其中密文：MD5(明文密码+jadada236t2) 后大写
        //明文密码
        String pwd ="H5CD3zE6" ;
        //密文
        String secretText = calculateDigest(pwd,"jadada236t2").toUpperCase();
        String digest = calculateDigest(customerCode+secretText,privateKey);

        JTCourierOrderDto dto = new JTCourierOrderDto();
        dto.setCustomerCode(customerCode);
        dto.setDigest(digest);

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
