package com.cmhq.core.controller;

import com.cmhq.core.api.dto.request.StoPushOrderStateDto;
import com.cmhq.core.api.dto.request.StoPushTraceDto;
import com.cmhq.core.api.strategy.StrategyFactory;
import com.cmhq.core.api.strategy.apipush.ApiPushTypeEumn;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.APIResponse;
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
    public APIResponse<String> pushOrderStatus(@Validated @RequestBody StoPushOrderStateDto dto)  {
        StrategyFactory.getApiPush(ApiPushTypeEumn.TYPE_STO_PUSHORDERSTATUS).pushHandle(dto);
        return APIResponse.success();
    }



    @ApiOperation("物流轨迹推送")
    @PostMapping(value = "/pushTrace")
    @AnonymousAccess
    public APIResponse<String> pushTrace(@Validated @RequestBody StoPushTraceDto dto) throws Exception {
        StrategyFactory.getApiPush(ApiPushTypeEumn.TYPE_STO_PUSHTRACE).pushHandle(dto);
        return APIResponse.success();
    }

}
