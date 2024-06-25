package com.cmhq.core.model;
import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName("fa_product_category")
@Data
public class FaProductEntity {
    @ExcelIgnore
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**商品编码*/
    @ExcelProperty("商品编码")
    private String categoreCode;

    /**商品名称*/
    @ExcelProperty("商品名称*")
    private String categoryName;

    /**重量*/
    @ExcelProperty("重量(kg)*")
    private Double weight;
    /**长度*/
    @ExcelProperty("长(cm)")
    private Double length;
    /**宽度*/
    @ExcelProperty("宽(cm)")
    private Double width;
    /**高*/
    @ExcelProperty("高(cm)")
    private Double height;
/**体积重 长*宽*高(cm)/8000*/
    @ExcelProperty("体积重(cm³)")
    private Double volume;

    private Integer cid;

    private Integer status=1;

    private Integer jituType=-1;

    private Integer pid=0;
}
