package com.cmhq.app.controller;

import com.cmhq.app.dao.HomeDao;
import com.cmhq.app.model.param.HomeOrderQuery;
import com.cmhq.app.model.param.HomeQuery;
import com.cmhq.app.model.rsp.HomeCompanyRsp;
import com.cmhq.app.model.rsp.HomeOrderRsp;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.APIResponse;
import me.zhengjie.QueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Api(value = "首页", tags = {"首页"})
@Slf4j
@RequestMapping("/api/index/")
public class HomePageComtroller {
    @Autowired
    private HomeDao homeDao;

    @ApiOperation("商户列表")
    @GetMapping(value = "indexCompany")
    public APIResponse list(@ModelAttribute HomeQuery query) {
        PageHelper.startPage(query.getPage(), query.getLimit());
        List<HomeCompanyRsp> list = homeDao.selectCompanyList(query);
        PageInfo<HomeCompanyRsp> page = new PageInfo<>(list);
        QueryResult<HomeCompanyRsp> queryResult = new QueryResult<>();
        queryResult.setItems(list);
        queryResult.setTotal(page.getTotal());
        return APIResponse.success(queryResult);
    }

    @ApiOperation("订单列表")
    @GetMapping(value = "indexFenxiao")
    public APIResponse indexFenxiao(@ModelAttribute HomeOrderQuery query) {
        PageHelper.startPage(query.getPage(), query.getLimit());
        List<HomeOrderRsp> list = homeDao.selectOrderList(query);
        PageInfo<HomeOrderRsp> page = new PageInfo<>(list);
        QueryResult<HomeOrderRsp> queryResult = new QueryResult<>();
        queryResult.setItems(list);
        queryResult.setTotal(page.getTotal());
        return APIResponse.success(queryResult);
    }
}
