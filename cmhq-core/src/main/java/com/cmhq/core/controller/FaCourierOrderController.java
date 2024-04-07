package com.cmhq.core.controller;

import com.cmhq.core.model.FaCourierOrderEntity;
import com.cmhq.core.model.param.CourierOrderQuery;
import com.cmhq.core.service.FaCourierOrderService;
import me.zhengjie.APIResponse;
import me.zhengjie.annotation.Log;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;
/**
 * Created by Jiyang.Zheng on 2024/4/6 13:48.
 */
@RestController
@RequiredArgsConstructor
@Api(tags = "CourierOrder管理")
@RequestMapping("/api/faCourierOrder")
public class FaCourierOrderController {

    @Autowired
    private final FaCourierOrderService faCourierOrderService;


    @PostMapping
    @Log("查询CourierOrder")
    @ApiOperation("查询CourierOrder")
    @PreAuthorize("@el.check('faCourierOrder:list')")
    public APIResponse queryFaCourierOrder(@RequestBody CourierOrderQuery query) {
        return APIResponse.success(faCourierOrderService.queryAll(query));
    }

    @PostMapping
    @Log("新增CourierOrder")
    @ApiOperation("新增CourierOrder")
    @PreAuthorize("@el.check('faCourierOrder:add')")
    public APIResponse createFaCourierOrder(@Validated @RequestBody FaCourierOrderEntity resources) {
        Integer id = faCourierOrderService.create(resources);
        return APIResponse.success(id);
    }

    @PostMapping
    @Log("查询运费CourierOrder")
    @ApiOperation("查询运费CourierOrder")
    @PreAuthorize("@el.check('faCourierOrder:list')")
    public APIResponse getCourierFreightCharge(@Validated @RequestBody FaCourierOrderEntity resources) {
        String c = faCourierOrderService.getCourierFreightCharge(resources);
        return APIResponse.success(c);
    }

    @ApiOperation("取消订单")
    @PreAuthorize("@el.check('faCourierOrder:add')")
    @RequestMapping(value = "cancelCourierOrder", method = RequestMethod.GET)
    public APIResponse cancelCourierOrder( @ApiParam(value = "id") @RequestParam() Integer id) {
        return APIResponse.success(faCourierOrderService.cancelCourierOrder(id));
    }
//
//    @Log("导出数据")
//    @ApiOperation("导出数据")
//    @GetMapping(value = "/download")
//    @PreAuthorize("@el.check('faCourierOrder:list')")
//    public void exportFaCourierOrder(HttpServletResponse response, FaCourierOrderQueryCriteria criteria) throws IOException {
//        faCourierOrderService.download(faCourierOrderService.queryAll(criteria), response);
//    }
}
