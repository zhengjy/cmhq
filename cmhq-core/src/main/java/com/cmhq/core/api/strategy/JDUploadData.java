package com.cmhq.core.api.strategy;

import com.cmhq.core.api.UploadData;

/**
 * Created by Jiyang.Zheng on 2024/4/19 16:39.
 */
public  class JDUploadData<T > extends UploadData {

    private T request;

    private String unKey2;


    public T getRequest() {
        return request;
    }

    public void setRequest(T request) {
        this.request = request;
    }

    public String getUnKey2() {
        return unKey2;
    }

    public void setUnKey2(String unKey2) {
        this.unKey2 = unKey2;
    }

    @Override
    public String getUnKeyValue() {
        return unKey2;
    }
}
