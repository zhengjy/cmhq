package com.cmhq.core.api.strategy;

import com.alibaba.fastjson.JSONObject;
import com.cmhq.core.api.UploadTypeEnum;
import com.cmhq.core.api.dto.StoCourierOrderDto;
import com.cmhq.core.api.dto.StoFreightChargeDto;
import com.cmhq.core.model.FaCourierOrderEntity;
import org.springframework.stereotype.Component;


/**查询申通时效运费
 * Created by Jiyang.Zheng on 2024/4/7 13:56.
 */
@Component
public class StoQueryFreightCharge extends AbstractStoUpload<FaCourierOrderEntity, StoFreightChargeDto>{
    @Override
    public UploadTypeEnum supports() {
        return UploadTypeEnum.TYPE_STO_QUERY_FREIGHT_CHARGE;
    }

    @Override
    protected String getUploadUrl() {
        return "QUERY_SEND_SERVICE_DETAIL";
    }


    @Override
    protected StoFreightChargeDto getData(FaCourierOrderEntity param) throws RuntimeException {
        JSONObject map = JSONObject.parseObject(param.getCourierOrderExtend());
        if (map == null){
            map = new JSONObject();
        }
        StoCourierOrderDto dto = new StoCourierOrderDto();
        dto.setOrderNo(param.getOrderNo());
        dto.setBillType((String) map.get("billType"));
        dto.setOrderType((String) map.get("orderType"));
        StoFreightChargeDto m = new StoFreightChargeDto();
        m.setSendName(param.getFromName());
        m.setSendMobile(param.getFromMobile());
        m.setSendProv(param.getFromProv());
        m.setSendCity(param.getFromCity());
        m.setSendArea(param.getFromArea());
        m.setSendAddress(param.getFromAddress());
        m.setRecName(param.getToName());
        m.setRecMobile(param.getToMobile());
        m.setRecProv(param.getToProv());
        m.setRecCity(param.getToCity());
        m.setRecArea(param.getToArea());
        m.setRecAddress(param.getToAddress());
        m.setOpenId(param.getOrderNo());
        m.setWeight(param.getWeight()+"");
        return m;
    }

    @Override
    protected String getToCode() {
        return "ORDERMS_API";
    }
}
