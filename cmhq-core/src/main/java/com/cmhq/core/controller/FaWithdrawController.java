package com.cmhq.core.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cmhq.core.dao.FaUserDao;
import com.cmhq.core.dao.FaWithdrawDao;
import com.cmhq.core.model.FaWithdrawEntity;
import com.cmhq.core.model.param.FcWithdrawQuery;
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

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by Jiyang.Zheng on 2020/4/28 13:42.
 */
@RestController
@RequestMapping("withdraw/")
@Api(value = "提现管理", tags = {"提现管理"})
@Slf4j

public class FaWithdrawController {

    @Autowired
    private FaWithdrawDao faWithdrawDao;
    @Autowired
    private FaUserDao faUserDao;

    @ApiOperation("列表")
    @RequestMapping(value = "list", method = RequestMethod.GET)
    public APIResponse list(FcWithdrawQuery query) {
        PageHelper.startPage(query.getPageNo(), query.getPageSize());
        LambdaQueryWrapper<FaWithdrawEntity> lam = new LambdaQueryWrapper<>();
        if (StringUtils.isNotEmpty(query.getUsername())){
            lam.inSql(FaWithdrawEntity::getUserId,"select id in from fa_user username="+query.getUsername());
        }
        if (query.getStatus() != null){
            lam.like(FaWithdrawEntity::getStatus,query.getStatus());
        }

        List<FaWithdrawEntity> list = faWithdrawDao.selectList(lam);
        PageInfo<FaWithdrawEntity> page = new PageInfo<>(list);
        QueryResult<FaWithdrawEntity> queryResult = new QueryResult<>();
        queryResult.setItems(list);
        queryResult.setTotal(page.getTotal());
        return APIResponse.success(queryResult);
    }

    @ApiOperation("状态更新")
    @Log("状态更新")
    @GetMapping("updateState")
    public APIResponse cancelCourierOrder(@ApiParam(value = "") @RequestParam() Integer status,
                                          @ApiParam(value = "备注") @RequestParam(required = false) String content,
                                          @ApiParam(value = "id") @RequestParam() Integer id) {
        FaWithdrawEntity fw = new FaWithdrawEntity();
        fw.setId(id);
        fw.setStatus(status);
        fw.setMsg(content);
        fw.setUpdateTime(LocalDateTime.now().toString());
        faWithdrawDao.updateById(fw);
        return APIResponse.success();
    }



}

