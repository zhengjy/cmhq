package com.cmhq.core.api.strategy;

import com.cmhq.core.api.UploadResult;
import com.cmhq.core.api.UploadTypeEnum;
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
public class StoCancelCourierOrder extends AbstractStoUpload<FaCourierOrderEntity, StoCancelCourierOrderDto>{
    @Autowired
    private FaCourierOrderDao faCourierOrderDao;
    @Override
    public UploadTypeEnum supports() {
        return UploadTypeEnum.TYPE_STO_CANCEL_COURIER_ORDER;
    }

    @Override
    protected String getUploadUrl() {
        return "EDI_MODIFY_ORDER_CANCEL";
    }


    @Override
    protected StoCancelCourierOrderDto getData(FaCourierOrderEntity entity) throws RuntimeException {
        StoCancelCourierOrderDto dto = new StoCancelCourierOrderDto();
        dto.setBillCode(entity.getCourierCompanyWaybillNo());
        dto.setOrderSource("KB");
        dto.setRemark(entity.getReason());

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

    @Override
    protected String getToCode() {
        return "edi_modify_order";
    }
}
