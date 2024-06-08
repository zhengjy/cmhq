package com.cmhq.core.model.param;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;


/**
 * Created by Jiyang.Zheng on 2024/6/6 19:00.
 */
@Data
public class CourierOrderImport {
    //@Length(min = 17, max = 17, message = "，请修改后重新导入")
    //@Pattern(regexp = "浙|山东唐骏", message = "不满足要求")
    //@Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "日期格式应该为yyyy-MM-dd，请修改后重新导入")
    @ExcelProperty("物品分类")
    private String jtGoodsType;
    @ExcelProperty("物品类型")
    private String goodsType;
    @ExcelProperty("产品名称编码")
    private String goodsCode;
    @ExcelProperty("产品名称")
    private String goodsName;

    @ExcelProperty(value = "重量(kg)")
    private Double weight;
    @ExcelProperty(value = "长(cm)")
    private Integer length;
    @ExcelProperty(value = "宽(cm)")
    private Integer width;
    @ExcelProperty(value = "高(cm)")
    private Integer height;

    @ExcelProperty(value = "上门取件时间")
    private String takeGoodsTime;
    @ExcelProperty(value = "上门取件结束时间")
    private String takeGoodsTimeEnd;

    @ExcelProperty("发货地址*")
    private String fromAddress;
    @ExcelProperty("收货地址*")
    private String toAddress;
    
    @ExcelProperty(value = "发货分区号")
    private String fromPartitionNumber;
    @ExcelProperty(value = "收货分区号")
    private String toPartitionNumber;
    @ExcelProperty(value = "备注")
    private String msg;


}
