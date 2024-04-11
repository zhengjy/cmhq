package com.cmhq.core.api.strategy.apipush;

/**
 * Created by Jiyang.Zheng on 2024/4/11 18:13.
 */
public interface ApiPush<Param> {

    ApiPushTypeEumn supports();


    void pushHandle(Param param);
}
