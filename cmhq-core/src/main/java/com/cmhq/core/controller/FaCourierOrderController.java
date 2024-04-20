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
import me.zhengjie.utils.SecurityUtils;
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
    public APIResponse cancelCourierOrder(@ApiParam(value = "uncancel:取消取消状态,cancel:取消状态，auditSuccess:审核成功，auditFail:审核失败") @RequestParam() String stateType,
                                          @ApiParam(value = "备注") @RequestParam(required = false) String content,
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
            if (SecurityUtils.getCurrentCompanyId() == null){
                map.put("预估费用", e.getEstimatePrice());//
            }
            map.put("实际费用", e.getPrice());
            map.put("期望上门取件时间", e.getTakeGoodsTime());
            //1上门取件2门店自寄
            map.put("寄件方式", e.getType() == 1 ? "上门取件" : "门店自寄");
            //0待取件1已取件2已发出3已签收
            String oname= "";
            if (e.getOrderState() == 0){
                oname = "待取件";
            }else if (e.getOrderState() == 1){
                oname = "已取件";
            }else if (e.getOrderState() == 2){
                oname = "已发出";
            }else if (e.getOrderState() == 3){
                oname = "已签收";
            }
            map.put("订单状态", oname);
            map.put("发货人", e.getFromName());
            map.put("发货手机号", e.getFromMobile());
            map.put("发出省份", e.getFromProv());
            map.put("发出城市", e.getFromCity());
            map.put("发出区县", e.getFromArea());
            map.put("发出地址", e.getFromAddress());
            map.put("收货人", e.getToName());
            map.put("收货手机号", e.getToMobile());
            map.put("收货省份", e.getToProv());
            map.put("收货城市", e.getToCity());
            map.put("收货区县", e.getToArea());
            map.put("收货地址", e.getToAddress());
            //1运输中2派件中3已签收
            String wname= "";
            if (e.getWuliuState() == 1){
                wname = "运输中";
            }else if (e.getWuliuState() == 2){
                wname = "派件中";
            }else if (e.getWuliuState() == 3){
                wname = "已签收";
            }
            map.put("物流状态", wname);

            //物流公司订单状态：1未调派业务员2已调派业务员3已揽收4已取件5已取消
            String csname= "";
            if (e.getCourierOrderState() == 1){
                csname = "未调派业务员";
            }else if (e.getCourierOrderState() == 2){
                csname = "已调派业务员";
            }else if (e.getCourierOrderState() == 3){
                csname = "已揽收";
            }else if (e.getCourierOrderState() == 4){
                csname = "已取件";
            }else if (e.getCourierOrderState() == 5){
                csname = "已取消";
            }
            map.put("物流公司订单状态", csname);
            //0异常1正常
            map.put("是否异常件", e.getOrderIsError() == 0 ? "异常" : "正常");
            //-1:待审核,0取消待审核1正常2审核通过3审核不通过
            String caname= "";
            if (e.getCancelOrderState() == -1){
                caname = "未调派业务员";
            }else if (e.getCancelOrderState() == 0){
                caname = "已调派业务员";
            }else if (e.getCancelOrderState() == 1){
                caname = "已揽收";
            }else if (e.getCancelOrderState() == 2){
                caname = "已取件";
            }else if (e.getCancelOrderState() == 3){
                caname = "已取消";
            }
            map.put("取消状态", caname);
            //1自己取消2物流公司取消
            map.put("取消类型", e.getCancelType() == 1 ? "自己取消" : "物流公司取消");
            map.put("取消时间", DateUtil.format(e.getCancelTime(), "yyyy-MM-dd mm:hh:ss"));
            map.put("取消原因", e.getReason());
            map.put("取件时间", e.getQjTime());
            map.put("签收时间", e.getQsTime());
            //0未结算1已结算
            map.put("是否结算", e.getIsJiesuan() == 1 ? "已结算" : "未结算");
            map.put("创建人", e.getCreateUserName());
            map.put("创建日期", DateUtil.format(e.getCreateTime(), "yyyy-MM-dd mm:hh:ss"));
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}
