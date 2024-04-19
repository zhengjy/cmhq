package com.cmhq.core.api.strategy;

import com.alibaba.fastjson.JSONObject;
import com.cmhq.core.api.UploadResult;
import com.cmhq.core.api.UploadTypeEnum;
import com.cmhq.core.api.dto.JTCancelCourierOrderDto;
import com.cmhq.core.api.dto.StoCancelCourierOrderDto;
import com.cmhq.core.dao.FaCourierOrderDao;
import com.cmhq.core.model.FaCourierOrderEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * Created by Jiyang.Zheng on 2024/4/7 13:56.
 */
@Component
public class JTCancelCourierOrder extends AbstractJTUpload<FaCourierOrderEntity, JTCancelCourierOrderDto>{
    @Autowired
    private FaCourierOrderDao faCourierOrderDao;
    @Override
    public UploadTypeEnum supports() {
        return UploadTypeEnum.TYPE_JT_CANCEL_COURIER_ORDER;
    }

    @Override
    protected String getUploadUrl() {
        return "/api/order/cancelOrder";
    }


    @Override
    protected JTCancelCourierOrderDto getData(FaCourierOrderEntity entity) throws RuntimeException {
        JTCancelCourierOrderDto dto = new JTCancelCourierOrderDto();
        dto.setTxlogisticId(entity.getOrderNo());
        dto.setReason(entity.getReason());
        return dto;
    }

    @Override
    public void uploadResultHandle(JTCancelCourierOrderDto uploadData, UploadResult uploadResult) {
        Object json = uploadResult.getJsonMsg();
        JSONObject jsonObject = JSONObject.parseObject(json+"",JSONObject.class);
        if (StringUtils.isEmpty(jsonObject.getString("billCode"))) {
            uploadResult.setFlag(false);
        }
        super.uploadResultHandle(uploadData,uploadResult);
    }
}
