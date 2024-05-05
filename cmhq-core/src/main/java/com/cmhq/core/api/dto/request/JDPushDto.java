package com.cmhq.core.api.dto.request;

import com.cmhq.core.api.UploadData;

public class JDPushDto<T> extends UploadData {

    private T obj;

    private String unKeyValue2;

    public String getUnKeyValue2() {
        return unKeyValue2;
    }

    public void setUnKeyValue2(String unKeyValue2) {
        this.unKeyValue2 = unKeyValue2;
    }

    public T getObj() {
        return obj;
    }

    public void setObj(T obj) {
        this.obj = obj;
    }

    @Override
    public String getUnKeyValue() {
        return unKeyValue2;
    }
}
