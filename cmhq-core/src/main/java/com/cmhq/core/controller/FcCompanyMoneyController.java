package com.cmhq.core.controller;

import cn.hutool.core.date.DateUtil;
import com.cmhq.core.model.FaCompanyEntity;
import com.cmhq.core.model.FaCompanyMoneyEntity;
import com.cmhq.core.model.FaRechargeEntity;
import com.cmhq.core.model.param.CompanyQuery;
import com.cmhq.core.model.param.FaRechargeQuery;
import com.cmhq.core.model.param.FcCompanyMoneyQuery;
import com.cmhq.core.service.FaCompanyMoneyService;
import com.cmhq.core.service.FaCompanyService;
import com.cmhq.core.util.CurrentUserContent;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.APIResponse;
import me.zhengjie.QueryResult;
import me.zhengjie.annotation.Log;
import me.zhengjie.modules.system.domain.User;
import me.zhengjie.utils.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    @ApiOperation("导出")
    @GetMapping(value = "/download")
    public void export(HttpServletResponse response, @ModelAttribute FcCompanyMoneyQuery query) throws IOException {
        query.setPageSize(10000);
        QueryResult<FaCompanyMoneyEntity> queryAll =  fcCompanyMoneyService.list(query);
        List<Map<String, Object>> list = new ArrayList<>();
        for (FaCompanyMoneyEntity e : queryAll.getItems()) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("公司名字", e.getCompanyName());
            map.put("金额", e.getOrderid());
            map.put("消费类型", e.getType() == 1 ? "系统" : e.getType() == 2 ? "充值" : "消费");
            map.put("增加或减少", e.getAddType() == 1 ? "增加" : e.getAddType() == 2 ? "减少" : "无");
            map.put("操作之前余额", e.getBefore());
            map.put("操作之后余额", e.getAfter());
            map.put("具体信息", e.getMsg());
            map.put("运单号", e.getBillCode());
            map.put("订单号", e.getOrderNo());
            map.put("创建日期", DateUtil.format(e.getCreateTime(), "yyyy-MM-dd mm:hh:ss"));
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

}

