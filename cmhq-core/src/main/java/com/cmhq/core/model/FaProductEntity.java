package com.cmhq.core.model;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName("fa_product_category")
@Data
public class FaProductEntity {

    @TableId(type = IdType.AUTO)
    private Integer id;

    /**商品编码*/
    @ExcelProperty("商品编码")
    private String categoreCode;

    /**商品名称*/
    @ExcelProperty("商品名称")
    private String categoryName;

    /**重量*/
    @ExcelProperty("重量")
    private Double weight;
    /**长度*/
    @ExcelProperty("长")
    private Integer length;
    /**宽度*/
    @ExcelProperty("宽")
    private Integer width;
    /**高*/
    @ExcelProperty("高")
    private Integer height;


    private Integer cid;

    private Integer status=1;

    private Integer jituType=-1;

    private Integer pid=0;
}
