package com.cmhq.core.api;

import lombok.Data;

/**
 * Created by Jiyang.Zheng on 2024/4/7 13:44.
 */
@Data
public class JTResponse {

    /**错误编码1:success 0:失败*/
    private String code;
    /**错误信息*/
    private String msg;

    private Object data;

    public boolean isSuccess(){
        return "1".equals(code);
    }
}
