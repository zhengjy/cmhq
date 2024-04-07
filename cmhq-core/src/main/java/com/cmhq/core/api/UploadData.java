package com.cmhq.core.api;

import com.alibaba.fastjson.annotation.JSONField;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by Jiyang.Zheng on 2024/4/7 10:38.
 */
public abstract class UploadData {
    @JSONField(serialize = false)
    private String unKey;
    @JSONField(serialize = false)
    public String getUnKey() {
        if (StringUtils.isNotEmpty(unKey)){
            return unKey;
        }
        return getUnKeyValue();
    }

    /***
     * 获取一条数据的唯一值，
     * 子类必须重写此方法
     * @return
     */
    @JSONField(serialize = false)
    public abstract String getUnKeyValue();
}
