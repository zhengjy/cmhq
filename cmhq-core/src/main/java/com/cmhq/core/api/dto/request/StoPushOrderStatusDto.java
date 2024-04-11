package com.cmhq.core.api.dto.request;

import lombok.Data;

/**https://open.sto.cn/#/apiDocument/EDI_PUSH_ORDER_STATUS
 * 申通订单调度状态推送
 * Created by Jiyang.Zheng on 2024/4/10 21:11.
 */
@Data
public class StoPushOrderStatusDto {
    /**业务类型：ORDER_STATUS_CHANGE_NOTIFY[接单事件]/ORDER_CANCEL_NOTIFY[取消订单事件]/ORDER_UPDATE_FETCHTIME_NOTIFY[修改取件时间事件]/ORDER_RETURN_NOTIFY[打回订单通知]*/
    private String event;
    /**网点/业务员 接单通知*/
    private ChangeInfo changeInfo;
    /**取消订单通知*/
    private CancelInfo cancelInfo;
    /**修改取件时间通知*/
    private ModifyInfo modifyInfo;
    /**打回订单通知*/
    private ReturnInfo returnInfo;

    // Getters and Setters
    @Data
    public static class ChangeInfo {
        /**订单号*/
        private String orderId;
        /**运单号*/
        private String billCode;
        /**订单调度状态（2-已调派/1-已分配/5-已完成/6-打回/4-已取消）*/
        private String status;
        /**接单网点名称*/
        private String siteName;
        /**接单网点编号*/
        private String siteCode;
        /**接单网点电话*/
        private String sitePhone;
        /**接单业务员编号*/
        private String userCode;
        /**接单业务员名称*/
        private String userName;
        /**业务员手机号*/
        private String userMobile;
        /**预约取件开始时间*/
        private String fetchStartTime;
        /**预约取件结束时间*/
        private String fetchEndTime;
        /**取件码*/
        private String printCode;

        // Getters and Setters
    }
    @Data
    public static class CancelInfo {
        /**订单号*/
        private String orderId;
        /**打回原因描述*/
        private String reason;
        /**运单号*/
        private String billCode;

        // Getters and Setters
    }
    @Data
    public static class ModifyInfo {
        /**订单号*/
        private String orderId;
        /**预约取件开始时间*/
        private String fetchStartTime;
        /**预约取件结束时间*/
        private String fetchEndTime;
        /**更改预约时间原因*/
        private String reason;
        /**原因编码*/
        private String code;

        // Getters and Setters
    }
    @Data
    public static class ReturnInfo {
        /**订单号*/
        private String orderId;
        /**打回原因描述*/
        private String reason;
        /**打回原因编号*/
        private String code;

        // Getters and Setters
    }
}
