package com.cmhq.core.controller;

import cn.hutool.core.date.DateUtil;
import com.cmhq.core.util.CurrentUserContent;
import com.cmhq.core.model.FaCompanyEntity;
import com.cmhq.core.model.param.CompanyQuery;
import com.cmhq.core.service.FaCompanyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.APIResponse;
import me.zhengjie.QueryResult;
import me.zhengjie.annotation.AnonymousAccess;
import me.zhengjie.annotation.Log;
import me.zhengjie.modules.system.domain.User;
import me.zhengjie.modules.system.service.UserService;
import me.zhengjie.modules.system.service.dto.JobSmallDto;
import me.zhengjie.modules.system.service.dto.RoleSmallDto;
import me.zhengjie.modules.system.service.dto.UserDto;
import me.zhengjie.modules.system.service.dto.UserQueryCriteria;
import me.zhengjie.utils.FileUtil;
import me.zhengjie.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @ApiOperation("导出")
    @GetMapping(value = "/download")
    public void export( HttpServletResponse response, @ModelAttribute CompanyQuery query) throws IOException {
        query.setPageSize(10000);
        QueryResult<FaCompanyEntity> queryAll =  faCompanyService.list(query);
        List<Map<String, Object>> list = new ArrayList<>();
        for (FaCompanyEntity e : queryAll.getItems()) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("公司名字", e.getCompanyName());
            map.put("手机号", e.getMobile());
            map.put("分销人", e.getFUserName());
            map.put("分销比例", e.getDistributionRatio());
            map.put("最多取消订单次数", e.getCancelMaxNum());
            map.put("单笔取消订单金额上限", e.getCancelMaxMoney());
            map.put("折扣比例", e.getRatio());
            map.put("余额", e.getMoney());
            map.put("冻结金额", e.getEstimatePrice());
            map.put("子账号名称", e.getName());
            map.put("子账号一天最大单量", e.getZiMaxNum());
            map.put("子账号每日最大订单金额", e.getZiMaxMoney());
            map.put("子账号单笔订单最大限制金额", e.getZiOneMaxMoney());
            map.put("创建日期", DateUtil.format(e.getCreateTime(), "yyyy-MM-dd mm:hh:ss"));
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

}

