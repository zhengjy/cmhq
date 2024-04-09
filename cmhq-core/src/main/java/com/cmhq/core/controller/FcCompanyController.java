package com.cmhq.core.controller;

import com.cmhq.core.util.CurrentUserContent;
import com.cmhq.core.model.FaCompanyEntity;
import com.cmhq.core.model.param.CompanyQuery;
import com.cmhq.core.service.FaCompanyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.APIResponse;
import me.zhengjie.annotation.AnonymousAccess;
import me.zhengjie.annotation.Log;
import me.zhengjie.modules.system.domain.User;
import me.zhengjie.modules.system.service.UserService;
import me.zhengjie.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Jiyang.Zheng on 2020/4/28 13:42.
 */
@RestController
@RequestMapping("company/")
@Api(value = "商户", tags = {"商户"})
@Slf4j

public class FcCompanyController {

    @Autowired
    private FaCompanyService faCompanyService;


    @ApiOperation("列表查询")
    @RequestMapping(value = "list", method = RequestMethod.GET)
    public APIResponse list(@ModelAttribute CompanyQuery query) {
        return APIResponse.success(faCompanyService.list(query));
    }
    @ApiOperation("获取当前商户信息")
    @RequestMapping(value = "currentCompany", method = RequestMethod.GET)
    public APIResponse currentCompany() {
        return APIResponse.success(CurrentUserContent.getCurrentCompany());
    }

    @Log("修改新增商户")
    @RequestMapping(value = "edit", method = RequestMethod.POST)
    public APIResponse edit( @RequestBody FaCompanyEntity entity) throws Exception {
        return APIResponse.success(faCompanyService.edit(entity));
    }


    @ApiOperation("删除商户")
    @RequestMapping(value = "delete", method = RequestMethod.GET)
    public APIResponse delete( @ApiParam(value = "id") @RequestParam() Integer id) {
        faCompanyService.delete(id);
        return APIResponse.success();
    }

    @ApiOperation("商户创建子账户")
    @RequestMapping(value = "createChildUser", method = RequestMethod.POST)
    public APIResponse createChildUser(@Validated @RequestBody User resources){
        faCompanyService.createChildUser(resources);
        return APIResponse.success();
    }



}

