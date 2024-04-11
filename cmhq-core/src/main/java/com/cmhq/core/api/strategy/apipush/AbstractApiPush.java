package com.cmhq.core.api.strategy.apipush;

/**
 * Created by Jiyang.Zheng on 2024/4/11 18:17.
 */
public abstract class AbstractApiPush<Req> implements ApiPush<Req> {

    @Override
    public void pushHandle(Req req) {
        //请求日志写入
        //获取状态


    }
}
