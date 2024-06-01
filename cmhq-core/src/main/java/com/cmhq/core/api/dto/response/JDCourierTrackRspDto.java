package com.cmhq.core.api.dto.response;

import lombok.Data;

import java.util.List;

@lombok.Data
public class JDCourierTrackRspDto {

    private Response response;
    @lombok.Data
    public static class Response{
        private Content content;
        private Integer code;

    }
    @lombok.Data
    public static class Content{
        private Integer code;
        private Data data;
        private String requestId;
        private String msg;
        private Boolean success;

    }
    @lombok.Data
    public static class Data{
        private String collectorName;
        private String collectorPhone;
        private List<TraceDetails> traceDetails;

    }
    @lombok.Data
    public static class TraceDetails{
        private Integer category;
        private String categoryName;
        private String operateSiteId;
        private String operationRemark;
        private String operationTime;
        private String operationTitle;
        private String operatorName;
        private String operatorPhone;
        private Integer state;
        private String waybillCode;

    }
}
