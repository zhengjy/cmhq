package com.cmhq.core.api.dto;

import com.cmhq.core.api.UploadData;
import lombok.Data;

/**计算费率
 * Created by Jiyang.Zheng on 2024/4/7 17:41.
 */
@Data
public class StoFreightChargeDto extends UploadData {
    private String SendName;
    private String SendMobile;
    private String SendProv;
    private String SendCity;
    private String SendArea;
    private String SendAddress;
    private String RecName;
    private String RecMobile;
    private String RecProv;
    private String RecCity;
    private String RecArea;
    private String RecAddress;
    private String OpenId;
    private String Weight;
    private String TakeUserCode;
    private String TakeCompanyCode;
    private String Length;
    private String Width;
    private String Height;
    private String AddOrderPageUrl;
    private String SystemCode;


    @Override
    public String getUnKeyValue() {
        return this.OpenId;
    }
}
