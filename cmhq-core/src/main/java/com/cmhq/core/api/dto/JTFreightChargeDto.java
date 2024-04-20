package com.cmhq.core.api.dto;

import lombok.Data;


/**计算费率
 * Created by Jiyang.Zheng on 2024/4/7 17:41.
 */
@Data
public class JTFreightChargeDto extends JTUploadData {
    private JTFreightChargeDto.Sender sender; // 发件人信息
    private JTFreightChargeDto.Receiver receiver; // 收件人信息
    private Double length; // 长度
    private Double width; // 宽度
    private Double height; // 高度
    /**重量，单位kg，范围0.01-30*/
    private Double weight; // 重量
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


    @Override
    public String getUnKeyValue() {
        return "JTFreightChargeDto";
    }
}
