package com.cmhq.core.api.dto.request;

import lombok.Data;

@Data
public class CourierOrderSubscribeTraceDto {
    private String waybillCode;
    private String mobile;
}
