package com.cmhq.core.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cmhq.core.dao.FaZiaddressDao;
import com.cmhq.core.model.FaZiaddressEntity;
import com.cmhq.core.model.param.ZiaddressQuery;
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
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by Jiyang.Zheng on 2020/4/28 13:42.
 */
@RestController
@RequestMapping("ziaddress/")
@Api(value = "地址庫", tags = {"地址庫"})
@Slf4j

public class FcZiaddressController {

    @Autowired
    private FaZiaddressDao faZiaddressDao;

    @ApiOperation("列表查询")
    @RequestMapping(value = "list", method = RequestMethod.GET)
    public APIResponse list(@ModelAttribute ZiaddressQuery query) {
        PageHelper.startPage(query.getPageNo(), query.getPageSize());
        LambdaQueryWrapper<FaZiaddressEntity> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotEmpty(query.getSearchTxt())){
            queryWrapper.like(FaZiaddressEntity::getAddress,query.getSearchTxt()).or().like(FaZiaddressEntity::getName,query.getSearchTxt());
        }
        List<FaZiaddressEntity> list = faZiaddressDao.selectList(queryWrapper);
        PageInfo<FaZiaddressEntity> page = new PageInfo<>(list);
        QueryResult<FaZiaddressEntity> queryResult = new QueryResult<>();
        queryResult.setItems(list);
        queryResult.setTotal(page.getTotal());
        return APIResponse.success(queryResult);
    }
    @Log("修改新增")
    @AnonymousAccess
    @RequestMapping(value = "edit", method = RequestMethod.POST)
    public APIResponse edit( @RequestBody FaZiaddressEntity entity) throws Exception {
        if (entity.getId() != null){
            faZiaddressDao.insert(entity);
        }else {
            faZiaddressDao.updateById(entity);
        }
        return APIResponse.success();
    }


    @ApiOperation("删除")
    @RequestMapping(value = "delete", method = RequestMethod.GET)
    public APIResponse delete( @ApiParam(value = "id") @RequestParam() Integer id) {
        faZiaddressDao.deleteById(id);
        return APIResponse.success();
    }



}

