package com.cmhq.core.api;

/**
 * Created by Jiyang.Zheng on 2022/8/29 13:37.
 */
public interface UploadCallback {
    void success(Object obj);
    void fail(Object obj,Exception e);
}
