package com.cmhq.core.controller;

import com.cmhq.core.model.FaCompanyEntity;
import com.cmhq.core.model.param.CompanyQuery;
import com.cmhq.core.model.param.FcCompanyMoneyQuery;
import com.cmhq.core.service.FaCompanyMoneyService;
import com.cmhq.core.service.FaCompanyService;
import com.cmhq.core.util.CurrentUserContent;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.APIResponse;
import me.zhengjie.annotation.Log;
import me.zhengjie.modules.system.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Jiyang.Zheng on 2020/4/28 13:42.
 */
@RestController
@RequestMapping("companyMoney/")
@Api(value = "充值消费记录表", tags = {"充值消费记录表"})
@Slf4j

public class FcCompanyMoneyController {

    @Autowired
    private FaCompanyMoneyService fcCompanyMoneyService;


    @ApiOperation("列表查询")
    @RequestMapping(value = "list", method = RequestMethod.GET)
    public APIResponse list(@ModelAttribute FcCompanyMoneyQuery query) {
        return APIResponse.success(fcCompanyMoneyService.list(query));
    }



    @ApiOperation("删除商户")
    @RequestMapping(value = "delete", method = RequestMethod.GET)
    public APIResponse delete( @ApiParam(value = "id") @RequestParam() Integer id) {
        fcCompanyMoneyService.delete(id);
        return APIResponse.success();
    }



}

