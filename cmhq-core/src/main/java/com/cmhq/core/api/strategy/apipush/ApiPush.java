package com.cmhq.core.api.strategy.apipush;

import com.cmhq.core.api.UploadData;

/**
 * Created by Jiyang.Zheng on 2024/4/11 18:13.
 */
public interface ApiPush<Param extends UploadData> {

    ApiPushTypeEumn supports();


    void pushHandle(Param param);
}
