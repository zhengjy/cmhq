package com.cmhq.core.api.dto.response;

import lombok.Data;

import java.util.List;

/**
 * sto物流信息返回映射
 * Created by Jiyang.Zheng on 2024/4/21 18:46.
 */
@Data
public class StoCourierTrackRspDto {
    // 运单信息列表
    private List<WaybillInfo> waybillNo;

    @Data
    public static class WaybillInfo {
        // 运单号
        private String waybillNo;

        // 扫描网点名称
        private String opOrgName;

        // 扫描网点编号
        private String opOrgCode;

        // 扫描网点所在城市
        private String opOrgCityName;

        // 扫描网点所在省份
        private String opOrgProvinceName;

        // 扫描网点电话
        private String opOrgTel;

        // 扫描时间
        private String opTime;

        // 扫描类型
        private String scanType;

        // 扫描员
        private String opEmpName;

        // 扫描员编号
        private String opEmpCode;

        // 轨迹描述信息
        private String memo;

        // 派件员或收件员姓名
        private String bizEmpName;

        // 派件员或收件员编号
        private String bizEmpCode;

        // 派件员或收件员电话
        private String bizEmpPhone;

        // 派件员或收件员电话
        private String bizEmpTel;

        // 下一网点名称
        private String nextOrgName;

        // 下一网点代码
        private String nextOrgCode;

        // 问题件原因名称
        private String issueName;

        // 签收人
        private String signoffPeople;

        // 重量，单位：kg
        private double weight;

        // 包号
        private String containerNo;

        // 订单组织代码
        private String orderOrgCode;

        // 订单组织名称
        private String orderOrgName;

        // 运输任务号
        private String transportTaskNo;

        // 车牌号
        private String carNo;

        // 运营组织类型代码
        private String opOrgTypeCode;

        // 合作伙伴名称
        private String partnerName;

        // getters 和 setters...
    }

}
