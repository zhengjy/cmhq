package com.cmhq.core.api.strategy.apipush;

import com.cmhq.core.api.JDResponse;
import com.cmhq.core.api.JTResponse;
import com.cmhq.core.api.StoResponse;
import com.cmhq.core.api.UploadData;

/**
 * Created by Jiyang.Zheng on 2024/4/11 18:13.
 */
public interface ApiPush<Param extends UploadData> {

    ApiPushTypeEumn supports();


    StoResponse stoPushHandle(Param param);
    JTResponse jtPushHandle(Param param);
    JDResponse jdPushHandle(Param param);
}
