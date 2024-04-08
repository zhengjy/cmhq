package com.cmhq.core.api.strategy;

import com.alibaba.fastjson.JSONObject;
import com.cmhq.core.api.StoResponse;
import com.cmhq.core.api.UploadResult;
import com.cmhq.core.api.UploadTypeEnum;
import com.cmhq.core.api.dto.StoCancelCourierOrderDto;
import com.cmhq.core.dao.FcCourierOrderDao;
import com.cmhq.core.model.FaCourierOrderEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;



/**
 * Created by Jiyang.Zheng on 2024/4/7 13:56.
 */
@Component
public class StoCancelCourierOrder extends AbstractStoUpload<Integer, StoCancelCourierOrderDto>{
    @Autowired
    private FcCourierOrderDao fcCourierOrderDao;
    @Override
    public UploadTypeEnum supports() {
        return UploadTypeEnum.TYPE_STO_CANCEL_COURIER_ORDER;
    }

    @Override
    protected String getUploadUrl() {
        return "EDI_MODIFY_ORDER_CANCEL";
    }


    @Override
    protected StoCancelCourierOrderDto getData(Integer id) throws RuntimeException {
        FaCourierOrderEntity entity  = fcCourierOrderDao.selectById(id);
        StoCancelCourierOrderDto dto = new StoCancelCourierOrderDto();
        dto.setBillCode(entity.getCourierCompanyWaybillNo());
        dto.setOrderSource("KB");

        return dto;
    }

    @Override
    public void uploadResultHandle(StoCancelCourierOrderDto uploadData, UploadResult uploadResult) {
        Object json = uploadResult.getJsonMsg();
        if (!StringUtils.equals(json+"", "成功")) {
            uploadResult.setFlag(false);
        }
        super.uploadResultHandle(uploadData,uploadResult);
    }
}
