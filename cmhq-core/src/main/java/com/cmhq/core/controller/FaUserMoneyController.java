package com.cmhq.core.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cmhq.core.dao.FaUserDao;
import com.cmhq.core.dao.FaUserMoneyDao;
import com.cmhq.core.model.FaCompanyCostEntity;
import com.cmhq.core.model.FaUserEntity;
import com.cmhq.core.model.FaUserMoneyEntity;
import com.cmhq.core.model.param.FcUserMoneryQuery;
import com.cmhq.core.model.param.FcUserQuery;
import com.cmhq.core.service.FaUserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.APIResponse;
import me.zhengjie.QueryResult;
import me.zhengjie.annotation.Log;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by Jiyang.Zheng on 2020/4/28 13:42.
 */
@RestController
@RequestMapping("userMoney/")
@Api(value = "分销人流水", tags = {"分销人流水"})
@Slf4j

public class FaUserMoneyController {

    @Autowired
    private FaUserMoneyDao faUserMoneyDao;

    @ApiOperation("列表")
    @RequestMapping(value = "list", method = RequestMethod.GET)
    public APIResponse list(FcUserMoneryQuery query) {
        PageHelper.startPage(query.getPageNo(), query.getPageSize());
        LambdaQueryWrapper<FaUserMoneyEntity> lam = new LambdaQueryWrapper<>();
        if (StringUtils.isNotEmpty(query.getUsername())){
            lam.inSql(FaUserMoneyEntity::getUserId,"select id in from fa_user username="+query.getUsername());
        }
        if (StringUtils.isNotEmpty(query.getBillCode())){
            lam.like(FaUserMoneyEntity::getBillCode,query.getBillCode());
        }
        if (StringUtils.isNotEmpty(query.getCompanyName())){
            lam.like(FaUserMoneyEntity::getCid,"select id in from fa_company username="+query.getUsername());
        }

        List<FaUserMoneyEntity> list = faUserMoneyDao.selectList(lam);
        PageInfo<FaUserMoneyEntity> page = new PageInfo<>(list);
        QueryResult<FaUserMoneyEntity> queryResult = new QueryResult<>();
        queryResult.setItems(list);
        queryResult.setTotal(page.getTotal());
        return APIResponse.success(queryResult);
    }



}

