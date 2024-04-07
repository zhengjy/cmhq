package com.cmhq.core.api.strategy;

import com.cmhq.core.api.UploadTypeEnum;
import com.cmhq.core.api.dto.BaiduAddressDto;
import com.cmhq.core.api.dto.StoCancelCourierOrderDto;
import com.cmhq.core.dao.FcCourierOrderDao;
import com.cmhq.core.model.FaCourierOrderEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Jiyang.Zheng on 2024/4/7 13:56.
 */
@Component
public class BaiduAddress extends AbstractBaiduUpload<String, BaiduAddressDto>{
    @Override
    public UploadTypeEnum supports() {
        return UploadTypeEnum.TYPE_BAIDU_ADDRDESS;
    }

    @Override
    protected String getUploadUrl() {
        return "/rpc/2.0/nlp/v1/address?access_token=";
    }


    @Override
    protected List<BaiduAddressDto> getData(String text) throws RuntimeException {
        BaiduAddressDto dto = new BaiduAddressDto();
        dto.setText(text);

        return Arrays.asList(dto);
    }
}
