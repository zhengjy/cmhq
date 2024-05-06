package com.cmhq.core.controller;

import com.alibaba.fastjson.JSONObject;
import com.cmhq.core.api.JDResponse;
import com.cmhq.core.api.JTResponse;
import com.cmhq.core.api.StoResponse;
import com.cmhq.core.api.dto.request.*;
import com.cmhq.core.api.strategy.StrategyFactory;
import com.cmhq.core.api.strategy.apipush.ApiPushTypeEumn;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.annotation.rest.AnonymousPostMapping;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Jiyang.Zheng on 2024/4/9 17:50.
 */
@RestController
@RequestMapping("v1/courierPushState/")
@Api(value = "外部接口-物流&订单状态接收", tags = {"外部接口-物流&订单状态接收"})
@Slf4j
public class ExternalInterfaceController {

    @ApiOperation("推送订单状态")
    @AnonymousPostMapping(value = "/pushOrderStatus")
    public StoResponse pushOrderStatus(@Validated @RequestBody StoPushOrderStateDto dto)  {
        StoResponse response = StrategyFactory.getApiPush(ApiPushTypeEumn.TYPE_STO_PUSHORDERSTATUS).stoPushHandle(dto);
        return response;
    }



    @ApiOperation("物流轨迹推送")
    @AnonymousPostMapping(value = "/pushTrace")
    public StoResponse pushTrace(@Validated @RequestBody StoPushTraceDto dto) throws Exception {
        ;
        return StrategyFactory.getApiPush(ApiPushTypeEumn.TYPE_STO_PUSHTRACE).stoPushHandle(dto);
    }

    @ApiOperation("结算重量回传")
    @AnonymousPostMapping(value = "/pushSettleWeight")
    public StoResponse pushSettleWeight(@Validated @RequestBody StoPushTraceDto dto) throws Exception {
        return StrategyFactory.getApiPush(ApiPushTypeEumn.TYPE_STO_PUSHSETTLEWEIGHT).stoPushHandle(dto);
    }



    @ApiOperation("极兔推送订单状态")
    @AnonymousPostMapping(value = "/jt/pushOrderStatus")
    public JTResponse jtPushOrderStatus(JTPushDto dto)  {
        log.info("req ="+ JSONObject.toJSONString(dto));

        return StrategyFactory.getApiPush(ApiPushTypeEumn.TYPE_JT_PUSHORDERSTATUS).jtPushHandle(dto);
    }


    @ApiOperation("极兔物流轨迹推送")
    @AnonymousPostMapping(value = "/jt/pushTrace")
    public JTResponse jtPushTrace(JTPushDto dto) throws Exception {
        return StrategyFactory.getApiPush(ApiPushTypeEumn.TYPE_JT_PUSHTRACE).jtPushHandle(dto);
    }

    @ApiOperation("极兔结算重量回传")
    @AnonymousPostMapping(value = "/jt/pushSettleWeight")
    public JTResponse jtPushSettleWeight(JTPushDto dto) throws Exception {
        return StrategyFactory.getApiPush(ApiPushTypeEumn.TYPE_JT_PUSHSETTLEWEIGHT).jtPushHandle(dto);
    }

    @ApiOperation("京东物流轨迹推送")
    @AnonymousPostMapping(value = "/jd/pushTrace")
    public JDResponse jtPushTrace(@RequestBody JDPushTraceDto dto) throws Exception {
        return StrategyFactory.getApiPush(ApiPushTypeEumn.TYPE_JD_PUSHTRACE).jdPushHandle(dto);
    }

}
