package com.cmhq.core.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cmhq.core.dao.FaProductDao;
import com.cmhq.core.model.FaProductEntity;
import com.cmhq.core.model.param.ProductQuery;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.APIResponse;
import me.zhengjie.QueryResult;
import me.zhengjie.annotation.AnonymousAccess;
import me.zhengjie.annotation.Log;
import me.zhengjie.annotation.rest.AnonymousGetMapping;
import me.zhengjie.utils.SecurityUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by Jiyang.Zheng on 2020/4/28 13:42.
 */
@RestController
@RequestMapping("product/")
@Api(value = "产品", tags = {"产品"})
@Slf4j

public class FaProductController {

    @Autowired
    private FaProductDao faProductDao;

    @ApiOperation("列表查询")
    @AnonymousGetMapping(value = "list")
    public APIResponse list(@ModelAttribute ProductQuery query) {
        PageHelper.startPage(query.getPageNo(), query.getPageSize());
        LambdaQueryWrapper<FaProductEntity> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotEmpty(query.getSearchTxt())){
            queryWrapper.like(FaProductEntity::getCategoryName,query.getSearchTxt());
        }
        if (query.getCompanyId() != null){
            queryWrapper.eq(FaProductEntity::getCid,query.getCompanyId());
        }else {
            if(SecurityUtils.getCurrentCompanyId() != null){
                queryWrapper.eq(FaProductEntity::getCid,SecurityUtils.getCurrentCompanyId());
            }
        }
        List<FaProductEntity> list = faProductDao.selectList(queryWrapper);
        PageInfo<FaProductEntity> page = new PageInfo<>(list);
        QueryResult<FaProductEntity> queryResult = new QueryResult<>();
        queryResult.setItems(list);
        queryResult.setTotal(page.getTotal());
        return APIResponse.success(queryResult);
    }
    @Log("修改新增")
    @AnonymousAccess
    @RequestMapping(value = "edit", method = RequestMethod.POST)
    public APIResponse edit( @RequestBody FaProductEntity entity) throws Exception {
        if (entity.getId() == null){
            entity.setCid(SecurityUtils.getCurrentCompanyId());
            faProductDao.insert(entity);
        }else {
            faProductDao.updateById(entity);
        }
        return APIResponse.success();
    }


    @ApiOperation("删除")
    @RequestMapping(value = "delete", method = RequestMethod.GET)
    public APIResponse delete( @ApiParam(value = "id") @RequestParam() Integer id) {
        faProductDao.deleteById(id);
        return APIResponse.success();
    }



}

