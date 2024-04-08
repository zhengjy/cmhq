package com.cmhq.core.model;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 上传日志
 */
@Data
@TableName("fa_upload_log")
public class UploadLogEntity {

    @TableId
    private Long id;
    /**上传类型*/
    private String uploadType;
    /**上传唯一标识*/
    private String uploadCode;
    /**上传消息*/
    private String msg;
    private String errorMsg;
    private Date createTime;

    private String uploadToken;
    private String createBy;

}
