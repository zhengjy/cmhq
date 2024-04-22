package com.cmhq.core.api.dto.request;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.cmhq.core.api.UploadData;
import lombok.Data;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by Jiyang.Zheng on 2024/4/21 15:52.
 */
@Data
public class JTPushDto<T>   extends UploadData {
    private String bizContent;

    private T obj;

    public T getObj(Class clazz) {
        Object o = JSONObject.parseObject(bizContent,clazz);
        return (T) o;
    }

    @Override
    public String getUnKeyValue() {
        return "JTPushDto";
    }
}
