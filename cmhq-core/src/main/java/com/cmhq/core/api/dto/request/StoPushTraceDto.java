package com.cmhq.core.api.dto.request;

import com.cmhq.core.api.UploadData;
import lombok.Data;

/**
 * 物流轨迹推送(STO推送到CP)
 * https://open.sto.cn/#/apiDocument/STO_TRACE_PLATFORM_PUSH
 * Created by Jiyang.Zheng on 2024/4/10 21:11.
 */
@Data
public class StoPushTraceDto extends UploadData {
    /**运单号*/
    private String waybillNo;
    /**订单号*/
    private String linkCode;
    /**轨迹实体*/
    private Trace trace;

    @Override
    public String getUnKeyValue() {
        return waybillNo;
    }

    @Data
    public static class Trace {
        /**业务主键，建议客户依据此业务主键做数据幂等,避免数据重复（因为数据接收成功但响应超时时，申通侧会触发超时异常，进而触发重推机制），不建议客户使用该字段做数据库主键*/
        private String outBizCode;
        /**当前网点名称*/
        private String opOrgName;
        /**当前网点编号*/
        private String opOrgCode;
        /**当前网点类型（1:网点、承包区 2:中转中心/分拨中心 3:代收点、门店、柜机）*/
        private String opOrgType;
        /**扫描网点所在城市名称*/
        private String opOrgCityName;
        /**扫描网点所在省份名称*/
        private String opOrgProvinceName;
        /**扫描网点联系电话*/
        private String opOrgTel;
        /**扫描时间（格式：yyyy-MM-dd HH:mm:ss）
         */
        private String opTime;
        /**
         扫描类型（收件;发件;到件;派件;第三方代派;问题件;退回件;留仓件;快件取出;代取快递; 派件入柜;柜机代收;驿站代收;第三方代收;签收;改地址件(2020-07-24 新增)）*/
        private String scanType;
        /**扫描员姓名*/
        private String opEmpName;
        /**扫描员编号*/
        private String opEmpCode;
        /**轨迹描述*/
        private String memo;
        /**代收点地址*/
        private String address;
        /**业务员姓名(scanType为收件、派件必传)*/
        private String bizEmpName;
        /**时区*/
        private String bizEmpCode;
        /***/
        private String bizEmpPhone;
        /***/
        private String bizEmpTel;
        /***/
        private String nextOrgName;
        /***/
        private String nextOrgCode;
        /***/
        private String nextOrgType;
        /***/
        private String issueName;
        /***/
        private String signoffPeople;
        /***/
        private double weight;
        /*时区**/
        private String tz;
        /***/
        private String remark;
    }
}
