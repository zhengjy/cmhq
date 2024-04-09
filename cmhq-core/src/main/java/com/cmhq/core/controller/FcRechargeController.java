package com.cmhq.core.controller;

import com.cmhq.core.model.FaCompanyEntity;
import com.cmhq.core.model.param.CompanyQuery;
import com.cmhq.core.model.param.FaRechargeQuery;
import com.cmhq.core.service.FaCompanyService;
import com.cmhq.core.service.FaRechargeService;
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

/**商户充值记录
 * Created by Jiyang.Zheng on 2020/4/28 13:42.
 */
@RestController
@RequestMapping("fcRecharge/")
@Api(value = "商户充值记录", tags = {"商户充值记录"})
@Slf4j

public class FcRechargeController {

    @Autowired
    private FaRechargeService faRechargeService;


    @ApiOperation("列表查询")
    @RequestMapping(value = "list", method = RequestMethod.GET)
    public APIResponse list(@ModelAttribute FaRechargeQuery query) {
        return APIResponse.success(faRechargeService.list(query));
    }

    @Log("修改新增商户")
    @RequestMapping(value = "edit", method = RequestMethod.POST)
    public APIResponse edit( @RequestBody FaCompanyEntity entity) throws Exception {
        return APIResponse.success(faRechargeService.edit(entity));
    }


    @ApiOperation("删除商户")
    @RequestMapping(value = "delete", method = RequestMethod.GET)
    public APIResponse delete( @ApiParam(value = "id") @RequestParam() Integer id) {
        faRechargeService.delete(id);
        return APIResponse.success();
    }




}

