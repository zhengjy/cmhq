package com.cmhq.core.api.strategy;

import com.cmhq.core.api.UploadTypeEnum;
import com.cmhq.core.api.UploadResult;

/**
 * Created by Jiyang.Zheng on 2024/4/7 10:32.
 */
public interface Upload<Req> {

    /**
     * 类型
     * @return
     */
    UploadTypeEnum supports();

    UploadResult execute(Req req);
}
