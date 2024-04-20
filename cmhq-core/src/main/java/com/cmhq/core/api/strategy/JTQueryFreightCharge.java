package com.cmhq.core.api.strategy;

import com.cmhq.core.api.UploadTypeEnum;
import com.cmhq.core.api.dto.JTFreightChargeDto;
import com.cmhq.core.model.FaCourierOrderEntity;
import org.springframework.stereotype.Component;


/**查询JT时效运费
 * Created by Jiyang.Zheng on 2024/4/7 13:56.
 */
@Component
public class JTQueryFreightCharge extends AbstractJTUpload<FaCourierOrderEntity, JTFreightChargeDto>{
    @Override
    public UploadTypeEnum supports() {
        return UploadTypeEnum.TYPE_JT_QUERY_FREIGHT_CHARGE;
    }

    @Override
    protected String getUploadUrl() {
        return "/api/spmComCost/getComCost";
    }


    @Override
    protected JTFreightChargeDto getData(FaCourierOrderEntity param) throws RuntimeException {
        JTFreightChargeDto dto = new JTFreightChargeDto();

        JTFreightChargeDto.Sender sender = new JTFreightChargeDto.Sender();
        dto.setSender(sender);
        sender.setName(param.getFromName());
        sender.setMobile(param.getFromMobile());
        sender.setProv(param.getFromProv());
        sender.setCity(param.getFromCity());
        sender.setArea(param.getFromArea());
        sender.setAddress(param.getFromAddress());

        JTFreightChargeDto.Receiver receiver = new JTFreightChargeDto.Receiver();
        dto.setReceiver(receiver);
        receiver.setName(param.getToName());
        receiver.setMobile(param.getToMobile());
        receiver.setProv(param.getToProv());
        receiver.setCity(param.getToCity());
        receiver.setArea(param.getToArea());
        receiver.setAddress(param.getToAddress());


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
