package com.cmhq.core.api.dto;

import lombok.Data;

/**
 * Created by Jiyang.Zheng on 2024/4/12 17:02.
 */
@Data
public class BaseStoDto  {
    /***/
    private String data_digest;
    /***/
    private String api_name;
    /***/
    private String from_appkey;
    /***/
    private String from_code;
    /***/
    private String to_appkey;
    /***/
    private String to_code;
    /***/
    private Object content;

}
