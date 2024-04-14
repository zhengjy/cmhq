package com.cmhq.core.api.dto.response;

import lombok.Data;

import java.util.List;

/**
 * Created by Jiyang.Zheng on 2024/4/13 22:05.
 */
@Data
public class CourierFreightChargeDto {
    private String sourceCode;
    private List<ServiceItem> availableServiceItemList;
    private String Ageing;

    // getters and setters ...

    @Data
    public static class ServiceItem {

        private TimeSelect tdTimeSelect;
        private String code;
        private FeeModel feeModel;
        private String title;
        private int version;

        // getters and setters ...
    }
    @Data
    public static class TimeSelect {

        private DeliveryServiceDetails realTime;
        private List<Appointment> appointTimes;

        // getters and setters ...
    }
    @Data
    public static class DeliveryServiceDetails {

        private int deliveryServiceId;
        private Object selectable;
        private String name;
        private String selectDisableTip;

        // getters and setters ...
    }
    @Data
    public static class Appointment {

        private String date;
        private boolean dateSelectable;
        private List<Time> timeList;

        // getters and setters ...
    }
    @Data
    public  static class Time {

        private boolean selectable;
        private String startTime;
        private String endTime;
        private String selectDisableTip;

        // getters and setters ...
    }

    /**
     * 价格模板实体
     */
    @Data
    public static class FeeModel {
        /**首重价格（单位分）*/
        private int startPrice;
        /**续重重量（单位克），重量调整粒度*/
        private int continuedHeavy;
        /**首重重量（单位克）*/
        private int startWeight;
        /**续重费用（单位分），每增加一份续重重量需要增加的价格*/
        private int continuedHeavyPrice;
        /**预估运费（单位分）*/
        private String totalPrice;

        // getters and setters ...
    }
}