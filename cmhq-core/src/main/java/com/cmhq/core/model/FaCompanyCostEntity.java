package com.cmhq.core.model;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@TableName("fa_company_cost")
@Data
public class FaCompanyCostEntity {
    @TableId(type = IdType.AUTO)
    @ExcelIgnore
    private Long id;
    @ExcelProperty("始发省份")
    private String proF; // 始发省
    @ExcelProperty("目的省份")
    private String proD; // 目的省
    @ExcelProperty("始发地")
    private String cityF;
    @ExcelProperty("目的地")
    private String cityD;
    @ExcelProperty("流向")
    private String address; // 流向
    @ExcelProperty("区域")
    private String area; // 区域
    @ExcelProperty("平台原价首重（1kg）")
    private Double priceInit; // 平台原价首重
    @ExcelProperty("平台原价续重（元/kg）")
    private Double priceInitTo; // 平台原价续重
    @ExcelProperty("首重（1kg）")
    private Double price; // 首重
    @ExcelProperty("续重（元/kg）")
    private Double priceTo;
    @ExcelProperty("快递公司编码")
    private String courierCompanyCode;

}
