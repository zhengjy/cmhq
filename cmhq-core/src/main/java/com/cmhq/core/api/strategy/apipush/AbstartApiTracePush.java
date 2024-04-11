package com.cmhq.core.api.strategy.apipush;

import com.cmhq.core.api.UploadData;
import com.cmhq.core.enums.CourierWuliuStateEnum;
import org.springframework.stereotype.Component;

/**
 * 物流状态处理
 * Created by Jiyang.Zheng on 2024/4/11 21:03.
 */
@Component
public abstract class AbstartApiTracePush< Req extends UploadData> extends AbstractApiPush<Req> {
    @Override
    protected void doPushHandle(Req req) {
        //获取状态
        CourierWuliuStateEnum wuliuStateEnum = getTraceState(req);
    }

    /**
     *
     * @param req
     * @return
     */
    protected abstract CourierWuliuStateEnum getTraceState(Req req);

    @Override
    protected void afterHandle(Req req) {

    }
}
