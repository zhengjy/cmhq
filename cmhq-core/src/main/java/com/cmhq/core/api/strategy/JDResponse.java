package com.cmhq.core.api.strategy;

import lombok.Data;

@Data
public class JDResponse<T> {
    private int code;
    private String msg;
    private String subMsg;
    private Boolean success;
    private T data;
    private Long mills;
    private String requestId;
    private String subCode;

}
