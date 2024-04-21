package com.cmhq.core.controller;

import com.cmhq.core.api.JTResponse;
import com.cmhq.core.api.StoResponse;
import com.cmhq.core.api.dto.request.JTPushDto;
import com.cmhq.core.api.dto.request.StoPushOrderStateDto;
import com.cmhq.core.api.dto.request.StoPushTraceDto;
import com.cmhq.core.api.strategy.StrategyFactory;
import com.cmhq.core.api.strategy.apipush.ApiPushTypeEumn;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.annotation.AnonymousAccess;
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
    @PostMapping(value = "/pushOrderStatus")
    @AnonymousAccess
    public StoResponse pushOrderStatus(@Validated @RequestBody StoPushOrderStateDto dto)  {
        StoResponse response = StrategyFactory.getApiPush(ApiPushTypeEumn.TYPE_STO_PUSHORDERSTATUS).stoPushHandle(dto);
        return response;
    }



    @ApiOperation("物流轨迹推送")
    @PostMapping(value = "/pushTrace")
    @AnonymousAccess
    public StoResponse pushTrace(@Validated @RequestBody StoPushTraceDto dto) throws Exception {
        ;
        return StrategyFactory.getApiPush(ApiPushTypeEumn.TYPE_STO_PUSHTRACE).stoPushHandle(dto);
    }

    @ApiOperation("结算重量回传")
    @PostMapping(value = "/pushSettleWeight")
    @AnonymousAccess
    public StoResponse pushSettleWeight(@Validated @RequestBody StoPushTraceDto dto) throws Exception {
        return StrategyFactory.getApiPush(ApiPushTypeEumn.TYPE_STO_PUSHSETTLEWEIGHT).stoPushHandle(dto);
    }



    @ApiOperation("极兔推送订单状态")
    @PostMapping(value = "/jt/pushOrderStatus")
    @AnonymousAccess
    public JTResponse jtPushOrderStatus(JTPushDto dto)  {

        return StrategyFactory.getApiPush(ApiPushTypeEumn.TYPE_JT_PUSHORDERSTATUS).jtPushHandle(dto);
    }


    @ApiOperation("极兔物流轨迹推送")
    @PostMapping(value = "/jt/pushTrace")
    @AnonymousAccess
    public JTResponse jtPushTrace(@Validated @RequestBody StoPushTraceDto dto) throws Exception {
        return StrategyFactory.getApiPush(ApiPushTypeEumn.TYPE_JT_PUSHTRACE).jtPushHandle(dto);
    }

    @ApiOperation("极兔结算重量回传")
    @PostMapping(value = "/jt/pushSettleWeight")
    @AnonymousAccess
    public JTResponse jtPushSettleWeight(@Validated @RequestBody StoPushTraceDto dto) throws Exception {
        return StrategyFactory.getApiPush(ApiPushTypeEumn.TYPE_JT_PUSHSETTLEWEIGHT).jtPushHandle(dto);
    }

}
