package com.cmhq.core.api.strategy;

import com.cmhq.core.api.UploadData;

/**
 * Created by Jiyang.Zheng on 2024/4/19 16:39.
 */
public abstract class JTUploadData extends UploadData {

    private String customerCode; // 客户编码
    private String digest; // 签名

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }
}
