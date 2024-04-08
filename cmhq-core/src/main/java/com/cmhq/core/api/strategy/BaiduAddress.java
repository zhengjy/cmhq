package com.cmhq.core.api.strategy;

import com.cmhq.core.api.UploadTypeEnum;
import com.cmhq.core.api.dto.BaiduAddressDto;
import org.springframework.stereotype.Component;


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
    protected BaiduAddressDto getData(String text) throws RuntimeException {
        BaiduAddressDto dto = new BaiduAddressDto();
        dto.setText(text);

        return dto;
    }

}
