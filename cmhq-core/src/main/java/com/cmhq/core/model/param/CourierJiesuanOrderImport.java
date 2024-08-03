package com.cmhq.core.model.param;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;


/**
 * Created by Jiyang.Zheng on 2024/6/6 19:00.
 */
@Data
public class CourierJiesuanOrderImport {
    public static final String FEE_TYPE_KDYF ="快递运费";
    public static final String FEE_TYPE_KDCCCC ="快递超长超重";
    public static final String FEE_TYPE_KDBZF ="包装费";
    public static final String FEE_TYPE_KDXSZT ="协商再投";

    @ExcelProperty("运单号")
    private String courierCompanyWaybillNo;
    @ExcelProperty(value = "计费重量")
    private Double weight;
    @ExcelProperty(value = "结算金额")
    private Double jiesuanMoney;

    @ExcelProperty(value = "费用类型")
    private String feeType;
    @ExcelProperty(value = "产品类型")
    private String productType;



}
