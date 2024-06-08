package com.cmhq.core.controller;

import com.cmhq.core.dao.FaUserDao;
import com.cmhq.core.model.FaUserEntity;
import com.cmhq.core.model.param.FcUserQuery;
import com.cmhq.core.service.FaUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.APIResponse;
import me.zhengjie.QueryResult;
import me.zhengjie.annotation.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Jiyang.Zheng on 2020/4/28 13:42.
 */
@RestController
@RequestMapping("fauser/")
@Api(value = "分销人", tags = {"分销人"})
@Slf4j

public class FaUserController {

    @Autowired
    private FaUserService faUserService;
    @Autowired
    private FaUserDao faUserDao;

    @ApiOperation("查询分销人")
    @RequestMapping(value = "list", method = RequestMethod.GET)
    public APIResponse list(FcUserQuery query) {
        QueryResult<FaUserEntity> queryQueryResult = faUserService.list(query);
        return APIResponse.success(queryQueryResult);
    }
    @Log("修改新增")
    @RequestMapping(value = "edit", method = RequestMethod.POST)
    public APIResponse edit( @RequestBody FaUserEntity entity) throws Exception {
        if (entity.getId() == null){
            faUserDao.insert(entity);
        }else {
            faUserDao.updateById(entity);
        }
        return APIResponse.success();
    }


    @ApiOperation("删除")
    @RequestMapping(value = "delete", method = RequestMethod.GET)
    public APIResponse delete( @ApiParam(value = "id") @RequestParam() Integer id) {
        faUserDao.deleteById(id);
        return APIResponse.success();
    }



}

