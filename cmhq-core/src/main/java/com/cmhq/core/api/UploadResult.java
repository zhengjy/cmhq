package com.cmhq.core.api;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Jiyang.Zheng on 2024/4/7 10:36.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class UploadResult {
    /**
     * 上传失败记录
     */
    @Builder.Default
    private List<String> fail = Collections.emptyList();
    @Builder.Default
    private List<String> failUks = Lists.newArrayList();
    @Builder.Default
    private List<String> successUks = Lists.newArrayList();
    private Object jsonMsg ;

    /**
     * 成功数量
     */
    private Integer successCount;
    /**
     * 失败数量
     */
    private Integer failCount;

    /**
     * 上传失败消息
     */
    private String errorMsg;
    /**
     * 返回json
     */
    private String errorJsonMsg;

    /**
     * 上传标记
     */
    private Boolean flag;

    /**
     * 单个上传对应失败记录
     */
    @Builder.Default
    private Map<String, String> errorMap = new HashMap<>();

    /**
     * 是否数组类型上传
     */
    private Boolean baseInfoUpload;


}
