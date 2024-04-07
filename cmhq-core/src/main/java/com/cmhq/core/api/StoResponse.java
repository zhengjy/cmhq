package com.cmhq.core.api;

import lombok.Data;

/**
 * Created by Jiyang.Zheng on 2024/4/7 13:44.
 */
@Data
public class StoResponse {

    /**是否成功*/
    private boolean success;
    /**错误编码*/
    private String errorCode;
    /**错误信息*/
    private String errorMsg;

    private Object data;
}
