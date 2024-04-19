package com.cmhq.core.api.dto;

import com.cmhq.core.api.UploadData;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Created by Jiyang.Zheng on 2024/4/7 13:57.
 */
@Data
public class JTCourierOrderDto extends JTUploadData {
    private String txlogisticId; // 客户订单号（传客户自己系统的订单号）
    private String expressType = "EZ"; // 快件类型：EZ(标准快递)TYD(兔优达)
    private String orderType = "2"; // 订单类型（有客户编号为月结）1、 散客；2、月结；
    private String serviceType = "01"; // 服务类型 ：02 门店寄件 ； 01 上门取件
    private String deliveryType = "03"; //  派送类型： 06 代收点自提 05 快递柜自提 04 站点自提 03 派送上门
    private String payType = "PP_PM"; // 支付类型 支付方式：PP_PM("寄付月结"), CC_CASH("到付现结");
    private Sender sender; // 发件人信息
    private Receiver receiver; // 收件人信息
    private String sendStartTime; // 物流公司上门取货开始时间 yyyy-MM-dd HH:mm:ss
    private String sendEndTime; // 客户物流公司上门取货结束时间 yyyy-MM-dd HH:mm:ss
    /**
     * 物品类型（对应订单主表物品类型）:
     * bm000001 文件
     * bm000002 数码产品
     * bm000003 生活用品
     * bm000004 食品
     * bm000005 服饰
     * bm000006 其他
     * bm000007 生鲜类
     * bm000008 易碎品
     * bm000009 液体
     */
    private String goodsType; //
    private Double length; // 长度
    private Double width; // 宽度
    private Double height; // 高度
    /**重量，单位kg，范围0.01-30*/
    private Double weight; // 重量
    private int totalQuantity =1; // 包裹总票数(必须为1)
    private List<Item> items; // 商品列表
    private String customsInfo; // 海关信息
    private String postSiteCode;
    private String postSiteName;
    private String postSiteAddress;
    private String realName;

    // Getters and Setters
    @Data
    public static class Sender {
        private String name; // 发件人姓名
        private String mobile; // 发件人手机号
        private String phone; //寄件电话（手机和电话二选一必填）
        private String countryCode = "CHN"; // 寄件国家三字码（如：中国=CHN、印尼=IDN）
        private String prov; // 发件人省份
        private String city; // 发件人城市
        private String area; // 发件人地区
        private String town;
        private String street;
        private String address; // 寄件详细地址（省+市+区县+详细地址）

        // Getters and Setters
    }
    @Data
    public static class Receiver {
        private String name; // 收件人姓名
        private String mobile; // 收件人手机号
        private String phone;//收件电话（手机和电话二选一必填）
        private String countryCode="CHN"; // 收件人国家编码
        private String prov; // 收件人省份
        private String city; // 收件人城市
        private String area; // 收件人地区
        private String town;
        private String street;
        private String address; // 收件详细地址（省+市+区县+详细地址）

        // Getters and Setters
    }
    @Data
    public static class Item {

        private String itemType;
        private String itemName;
        private String chineseName;
        private String englishName;
        private int number;
        private String itemValue;
        private String priceCurrency;
        private String desc;
        private String itemUrl;

        // Getters and Setters
    }


    @Override
    public String getUnKeyValue() {
        return txlogisticId;
    }
}
