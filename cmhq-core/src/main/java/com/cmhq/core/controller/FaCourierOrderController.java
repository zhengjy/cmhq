package com.cmhq.core.controller;

import cn.hutool.core.date.DateUtil;
import com.cmhq.core.model.FaCompanyMoneyEntity;
import com.cmhq.core.model.FaCourierOrderEntity;
import com.cmhq.core.model.param.CourierOrderQuery;
import com.cmhq.core.model.param.FcCompanyMoneyQuery;
import com.cmhq.core.service.FaCourierOrderService;
import me.zhengjie.APIResponse;
import me.zhengjie.QueryResult;
import me.zhengjie.annotation.AnonymousAccess;
import me.zhengjie.annotation.Log;
import me.zhengjie.utils.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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


    @GetMapping("list")
    @Log("查询CourierOrder")
    @ApiOperation("查询CourierOrder")
    public APIResponse queryFaCourierOrder(@ModelAttribute CourierOrderQuery query) {
        return APIResponse.success(faCourierOrderService.queryAll(query));
    }

    @PostMapping("create")
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


    @ApiOperation("导出")
    @GetMapping(value = "/download")
    public void export(HttpServletResponse response, @ModelAttribute CourierOrderQuery query) throws IOException {
        query.setPageSize(10000);
        QueryResult<FaCourierOrderEntity> queryAll =  faCourierOrderService.queryAll(query);
        List<Map<String, Object>> list = new ArrayList<>();
        for (FaCourierOrderEntity e : queryAll.getItems()) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("公司名字", e.getCompanyName());
            map.put("快递公司", e.getCourierCompanyCode());
            map.put("快递公司运单号", e.getCourierCompanyWaybillNo());
            map.put("物品名称", e.getGoodsName());
            map.put("重量", e.getWeight());
            map.put("宽度", e.getWidth());
            map.put("长度", e.getLength());
            map.put("高", e.getHeight());
            map.put("预估费用", e.getOrderNo());//
            map.put("实际费用", e.getOrderNo());
            map.put("期望上门取件时间", e.getOrderNo());
            map.put("寄件方式", e.getOrderNo());
            map.put("订单状态", e.getOrderNo());
            map.put("发货人", e.getOrderNo());
            map.put("发货手机号", e.getOrderNo());
            map.put("发出省份", e.getOrderNo());
            map.put("发出城市", e.getOrderNo());
            map.put("发出区县", e.getOrderNo());
            map.put("发出地址", e.getOrderNo());
            map.put("收货人", e.getOrderNo());
            map.put("收货手机号", e.getOrderNo());
            map.put("收货省份", e.getOrderNo());
            map.put("收货城市", e.getOrderNo());
            map.put("收货区县", e.getOrderNo());
            map.put("收货地址", e.getOrderNo());
            map.put("物流状态", e.getOrderNo());
            map.put("物流公司返回订单状态", e.getOrderNo());
            map.put("是否异常件", e.getOrderNo());
            map.put("取消状态", e.getOrderNo());
            map.put("取消类型", e.getOrderNo());
            map.put("取消时间", e.getOrderNo());
            map.put("取消原因", e.getOrderNo());
            map.put("取件时间", e.getOrderNo());
            map.put("签收时间", e.getOrderNo());
            map.put("是否结算", e.getOrderNo());
            map.put("创建人", e.getOrderNo());
            map.put("创建日期", DateUtil.format(e.getCreateTime(), "yyyy-MM-dd mm:hh:ss"));
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}
