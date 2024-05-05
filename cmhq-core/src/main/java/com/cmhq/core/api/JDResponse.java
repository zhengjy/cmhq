package com.cmhq.core.api;

import lombok.Data;

/**
 * Created by Jiyang.Zheng on 2024/4/7 13:44.
 */
@Data
public class JDResponse {

    /**错误编码200:成功*/
    private String code;
    /**错误信息*/
    private String msg;

    public boolean isSuccess(){
        return "200".equals(code);
    }
}
