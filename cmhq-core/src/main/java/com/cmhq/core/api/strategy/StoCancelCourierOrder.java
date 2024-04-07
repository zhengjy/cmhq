package com.cmhq.core.api.strategy;

import com.alibaba.fastjson.JSONObject;
import com.cmhq.core.api.UploadTypeEnum;
import com.cmhq.core.api.dto.StoCancelCourierOrderDto;
import com.cmhq.core.dao.FcCourierOrderDao;
import com.cmhq.core.model.FaCourierOrderEntity;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
    protected void syncTransferStatus(List<String> code, String status) {

    }

    @Override
    protected List<StoCancelCourierOrderDto> getData(Integer id) throws RuntimeException {
        FaCourierOrderEntity entity  = fcCourierOrderDao.selectById(id);
        StoCancelCourierOrderDto dto = new StoCancelCourierOrderDto();
        dto.setBillCode(entity.getCourierCompanyWaybillNo());
        dto.setOrderSource("KB");

        return Arrays.asList(dto);
    }
}
