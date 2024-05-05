package com.cmhq.core.api.dto.request;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.cmhq.core.api.UploadData;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by Jiyang.Zheng on 2024/4/21 15:52.
 */
public class JTPushDto<T>   extends UploadData {
    private String bizContent;
    private String unKeyValue3;

    private T obj;

    public T getObj(Class clazz) {
        Object o = JSONObject.parseObject(bizContent,clazz);
        return (T) o;
    }

    public String getBizContent() {
        return bizContent;
    }

    public void setBizContent(String bizContent) {
        JSONObject o = JSONObject.parseObject(bizContent,JSONObject.class);
        String orderId = o.getString("txlogisticId");
        if (StringUtils.isEmpty(orderId)){
            orderId = o.getString("billCode");
        }
        this.unKeyValue3 = orderId;
        this.bizContent = bizContent;
    }

    public String getUnKeyValue3() {
        return unKeyValue3;
    }

    public void setUnKeyValue3(String unKeyValue2) {
        this.unKeyValue3 = unKeyValue2;
    }

    @Override
    public String getUnKeyValue() {
        return getUnKeyValue3();
    }
}
