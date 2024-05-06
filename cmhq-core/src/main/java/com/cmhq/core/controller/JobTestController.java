package com.cmhq.core.controller;
import com.cmhq.core.quartz.CourierGetJDOrderStatusTask;
import com.cmhq.core.quartz.CourierOrderTask;
import com.cmhq.core.service.impl.FaCompanyServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.APIResponse;
import me.zhengjie.annotation.rest.AnonymousGetMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * Created by Jiyang.Zheng on 2020/4/28 13:42.
 */
@RestController
@RequestMapping("jobtest/")
@Api(value = "job", tags = {"job"})
@Slf4j

public class JobTestController {

    @Autowired
    private CourierOrderTask courierOrderTask;
    @Autowired
    private CourierGetJDOrderStatusTask courierGetJDOrderStatusTask;

    @ApiOperation("checkCourierTimeout24")
    @AnonymousGetMapping(value = "checkCourierTimeout24")
    public APIResponse checkCourierTimeout24() {
        courierOrderTask.checkCourierTimeout24();
        return APIResponse.success();
    }
    @ApiOperation("getJDOrderStatus")
    @AnonymousGetMapping(value = "getJDOrderStatus")
    public APIResponse courierGetJDOrderStatusTask() {
        courierGetJDOrderStatusTask.getJDOrderStatus();
        return APIResponse.success();
    }
    @ApiOperation("removeCache")
    @AnonymousGetMapping(value = "removeCache")
    public APIResponse removeCache() {
        FaCompanyServiceImpl.cache.cleanUp();
        return APIResponse.success();
    }



}

