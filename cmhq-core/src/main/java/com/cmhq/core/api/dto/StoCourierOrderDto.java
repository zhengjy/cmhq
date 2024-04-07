package com.cmhq.core.api.dto;

import com.cmhq.core.api.UploadData;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Created by Jiyang.Zheng on 2024/4/7 13:57.
 */
@Data
public class StoCourierOrderDto extends UploadData {
    /** 订单号（客户系统自己生成，唯一）*/
    private String orderNo;
    /**订单来源（订阅服务时填写的来源编码）     */
    private String orderSource;
    /** 获取面单的类型（00-普通、03-国际、01-代收、02-到付、04-生鲜），默认普通业务，如果有其他业务先与业务方沟通清楚*/
    private String billType;
    /**订单类型（01-普通订单、02-调度订单）默认01-普通订单，如果有散单业务需先业务方沟通清楚 */
    private String orderType;
    /**寄件人信息     */
    private Sender sender;
    /** 收件人信息*/
    private Receiver receiver;
    /**包裹信息 */
    private Cargo cargo;
    /** */
    private Customer customer;
    /** */
    private InternationalAnnex internationalAnnex;
    /** */
    private String waybillNo;
    /** */
    private AssignAnnex assignAnnex;
    /** */
    private String codValue;
    /** */
    private String freightCollectValue;
    /** */
    private String timelessType;
    /** */
    private String productType;
    /** */
    private List<String> serviceTypeList;
    /** */
    private Map<String, String> extendFieldMap;
    /** */
    private String remark;
    /** */
    private String expressDirection;
    /** */
    private String createChannel;
    /** */
    private String regionType;
    /** */
    private InsuredAnnex insuredAnnex;
    /** */
    private String expectValue;
    /** */
    private String payModel;
    @Data
    public static class Sender {
        /**寄件人名称 */
        private String name;
        /** */
        private String tel;
        /**寄件人手机号码 */
        private String mobile;
        /** */
        private String postCode;
        /** */
        private String country;
        /**省*/
        private String province;
        /** 市*/
        private String city;
        /**区  */
        private String area;
        /** */
        private String town;
        /** 详细地址*/
        private String address;
    }
    @Data
    public static class Receiver {
        /**收件人名称 */
        private String name;
        /** */
        private String tel;
        /**收件人手机号码 */
        private String mobile;
        /** */
        private String postCode;
        /** */
        private String country;
        /**省 */
        private String province;
        /**市 */
        private String city;
        /**区 */
        private String area;
        /** */
        private String town;
        /**详细地址         */
        private String address;
        /** */
        private String safeNo;
    }

    @Data
    public static class Cargo {
        /**带电标识 （10/未知 20/带电 30/不带电）*/
        private String battery;
        /**物品类型（大件、小件、扁平件\文件）*/
        private String goodsType;
        /**物品名称*/
        private String goodsName;
        /***/
        private Integer goodsCount;
        private Double spaceX;
        private Double spaceY;
        private Double spaceZ;
        private Double weight;
        private String goodsAmount;
        private List<CargoItem> cargoItemList;

        // getters and setters
    }
    @Data
    public static class CargoItem {
        private String serialNumber;
        private String referenceNumber;
        private String productId;
        private String name;
        private Integer qty;
        private Integer unitPrice;
        private Integer amount;
        private String currency;
        private Integer weight;
        private String remark;

        // getters and setters
    }
    @Data
    public static class Customer {
        private String siteCode;
        private String customerName;
        private String sitePwd;
        private String monthCustomerCode;

        // getters and setters
    }
    @Data
    public static class InternationalAnnex {
        private String internationalProductType;
        private boolean customsDeclaration;
        private String senderCountry;
        private String receiverCountry;

        // getters and setters
    }
    @Data
    public static class AssignAnnex {
        private String takeCompanyCode;
        private String takeUserCode;

        // getters and setters
    }
    @Data
    public static class InsuredAnnex {
        private String insuredValue;
        private String goodsValue;

        // getters and setters
    }


    @Override
    public String getUnKeyValue() {
        return this.orderNo;
    }
}
