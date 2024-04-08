package com.cmhq.core.controller;

import com.cmhq.core.model.FaCourierOrderEntity;
import com.cmhq.core.model.param.CourierOrderQuery;
import com.cmhq.core.service.FaCourierOrderService;
import me.zhengjie.APIResponse;
import me.zhengjie.annotation.AnonymousAccess;
import me.zhengjie.annotation.Log;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;
/**
 * Created by Jiyang.Zheng on 2024/4/6 13:48.
 */
@RestController
@RequiredArgsConstructor
@Api(tags = "CourierOrder管理")
@RequestMapping("courierOrder/")
@AnonymousAccess
public class FaCourierOrderController {

    @Autowired
    private final FaCourierOrderService faCourierOrderService;


    @PostMapping("queryFaCourierOrder")
    @Log("查询CourierOrder")
    @ApiOperation("查询CourierOrder")
    public APIResponse queryFaCourierOrder(@RequestBody CourierOrderQuery query) {
        return APIResponse.success(faCourierOrderService.queryAll(query));
    }

    @PostMapping("createFaCourierOrder")
    @Log("新增CourierOrder")
    @ApiOperation("新增CourierOrder")
    public APIResponse createFaCourierOrder(@Validated @RequestBody FaCourierOrderEntity resources) {
        Integer id = faCourierOrderService.create(resources);
        return APIResponse.success(id);
    }

    @PostMapping("getCourierFreightCharge")
    @Log("查询运费CourierOrder")
    @ApiOperation("查询运费CourierOrder")
    public APIResponse getCourierFreightCharge(@Validated @RequestBody FaCourierOrderEntity resources) {
        return APIResponse.success(faCourierOrderService.getCourierFreightCharge(resources));
    }

    @ApiOperation("状态更新")
    @Log("状态更新")
    @GetMapping("updateState")
    public APIResponse cancelCourierOrder(@ApiParam(value = "cancel:取消状态，auditSuccess:审核成功，auditFail:审核失败") @RequestParam() String stateType,
                                          @ApiParam(value = "备注") @RequestParam() String content,
                                          @ApiParam(value = "id") @RequestParam() Integer id) {
        return APIResponse.success(faCourierOrderService.cancelCourierOrder(stateType,content,id));
    }

    @ApiOperation("地址识别")
    @GetMapping("addressAnalysis")
    public APIResponse addressAnalysis( @ApiParam(value = "text") @RequestParam() String text) {
        return APIResponse.success(faCourierOrderService.addressAnalysis(text));
    }
    @ApiOperation("物流轨迹查询")
    @GetMapping("queryCourierTrack")
    public APIResponse queryCourierTrack( @ApiParam(value = "id") @RequestParam() Integer id) {
        return APIResponse.success(faCourierOrderService.queryCourierTrack(id));
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
