package com.cmhq.core.controller;

import com.cmhq.core.dao.FaRechargeDao;
import com.cmhq.core.model.FaRechargeEntity;
import com.cmhq.core.service.FaRechargeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.APIResponse;
import me.zhengjie.annotation.AnonymousAccess;
import me.zhengjie.annotation.Log;
import me.zhengjie.domain.AlipayConfig;
import me.zhengjie.domain.vo.TradeVo;
import me.zhengjie.service.AliPayService;
import me.zhengjie.utils.AliPayStatusEnum;
import me.zhengjie.utils.AlipayUtils;
import me.zhengjie.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

/**
 * Created by Jiyang.Zheng on 2024/4/9 17:50.
 */
@RestController
@RequestMapping("v1/sto/")
@Api(value = "外部接口-sto", tags = {"外部接口-sto"})
@Slf4j
public class StoInterfaceController {

    @ApiOperation("推送订单状态")
    @PostMapping(value = "/pushOrderStatus")
    @AnonymousAccess
    public APIResponse<String> pushOrderStatus(@Validated @RequestBody TradeVo trade)  {
        return APIResponse.success();
    }



    @ApiOperation("物流轨迹推送")
    @PostMapping(value = "/pushTrace")
    @AnonymousAccess
    public APIResponse<String> pushTrace(@Validated @RequestBody TradeVo trade) throws Exception {

        return APIResponse.success();
    }

}
