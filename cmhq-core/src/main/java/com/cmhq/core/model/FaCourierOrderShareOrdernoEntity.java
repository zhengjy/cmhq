package com.cmhq.core.model;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * Created by Jiyang.Zheng on 2024/4/8 14:11.
 */
@Data
@TableName("fa_courier_order_share_orderno")
public class FaCourierOrderShareOrdernoEntity {


    @TableId
    private String orderNo;
}
