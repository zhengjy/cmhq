package com.cmhq.core.api.dto.request;

import com.cmhq.core.api.UploadData;
import lombok.Data;

import java.util.List;

/**
 * 物流轨迹回传
 * https://open.jtexpress.com.cn/#/apiDoc/logistics/statusFeedback
 * Created by Jiyang.Zheng on 2024/4/10 21:11.
 */
@Data
public class JTPushTraceDto extends UploadData {

    //运单号
    private String billCode;

    // 快递轨迹详情
    private List<Detail> details;

    // 客户订单号
    private String txlogisticId;


    // 快递轨迹详情
    @Data
    public static class Detail {
        // 扫描时间
        private String scanTime;
        // 轨迹描述
        private String desc;

        // 扫描网点区/县
        private String scanNetworkArea;

        // 扫描网点城市
        private String scanNetworkCity;

        // 扫描网点联系方式
        private String scanNetworkContact;

        // 扫描网点ID
        private Integer scanNetworkId;

        // 扫描网点名称
        private String scanNetworkName;

        // 扫描网点省份
        private String scanNetworkProvince;

        // 网点类型名称
        private String scanNetworkTypeName;
        // 业务员姓名
        private String staffName;
        // 业务员联系方式
        private String staffContact;
        //上一站(到件)或下一站名称(发件)
        private String nextStopName;

        /**
         * 扫描类型
         * 1、快件揽收
         *
         * 2、入仓扫描（停用）
         *
         * 3、发件扫描
         *
         * 4、到件扫描
         *
         * 5、出仓扫描
         *
         * 6、入库扫描
         *
         * 7、代理点收入扫描
         *
         * 8、快件取出扫描
         *
         * 9、出库扫描
         *
         * 10、快件签收
         *
         * 11、问题件扫描
         *
         * 12、安检扫描
         *
         * 13、其他扫描
         * */
        private String scanType;

        /**
         * A1、客户取消寄件-网点
         *
         * A2、客户拒收
         *
         * A3、更改派送地址
         *
         * A4、退回件-网点
         *
         * A7、异常件提醒
         *
         * A8、包裹异常-网点
         *
         * A9、收件人联系不上
         *
         * A10、收件人联系不上
         *
         * A11、多次派件失败
         *
         * A12、收件人信息错误
         *
         * A13、更改派送时间
         *
         * A14、客户拒收
         *
         * A15、收件地址不详
         *
         * A16、收件地址错误
         *
         * A17、包裹存放至网点
         *
         * A18、包裹存放至网点
         *
         * A19、收件地址禁止
         *
         * A20、包裹异常
         *
         * A21、包裹暂存网点
         *
         * A23、包裹延迟-节假日
         *
         * A24、包裹异常-中心
         *
         * A25、退回件-中心
         *
         * A26、客户取消寄件-中心
         *
         * A27、异常天气
         *
         * A28、自然灾害
         *
         * A29、代收/到付结算异议拒付
         *
         * A30、货未到齐
         *
         * A31、作废件
         *
         * A32、未录进仓编码
         *
         * A33、政府管制
         *
         * A34、乡镇件
         *
         * A35、发票问题客户拒收
         */
        private String problemType;

    }

    @Override
    public String getUnKeyValue() {
        return billCode;
    }
}
