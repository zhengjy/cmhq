package com.cmhq.core.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class FaProductEntity {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String categoryName;

    private Integer cid;

    private Integer status=1;

    private Integer jituType=-1;

    private Integer pid=0;
}
