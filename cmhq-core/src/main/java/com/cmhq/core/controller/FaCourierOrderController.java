package com.cmhq.core.controller;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cmhq.core.dao.FaCourierOrderDao;
import com.cmhq.core.dao.FaCourierOrderShareOrdernoDao;
import com.cmhq.core.dao.FaRechargeDao;
import com.cmhq.core.model.FaCourierOrderEntity;
import com.cmhq.core.model.FaCourierOrderShareOrdernoEntity;
import com.cmhq.core.model.FaRechargeEntity;
import com.cmhq.core.model.param.CourierOrderQuery;
import com.cmhq.core.service.FaCourierOrderService;
import com.cmhq.core.util.EstimatePriceUtil;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.APIResponse;
import me.zhengjie.QueryResult;
import me.zhengjie.annotation.AnonymousAccess;
import me.zhengjie.annotation.Log;
import me.zhengjie.annotation.rest.AnonymousGetMapping;
import me.zhengjie.annotation.rest.AnonymousPostMapping;
import me.zhengjie.utils.FileUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * Created by Jiyang.Zheng on 2024/4/6 13:48.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@Api(tags = "CourierOrder管理")
@RequestMapping("courierOrder/")
@AnonymousAccess
public class FaCourierOrderController {

    @Autowired
    private final FaCourierOrderService faCourierOrderService;
    @Autowired
    private FaCourierOrderShareOrdernoDao faCourierOrderShareOrdernoDao;
    @Autowired
    private FaRechargeDao faRechargeDao;
    @Autowired
    private FaCourierOrderDao faCourierOrderDao;


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
    @PostMapping("batchCreate")
    @Log("新增CourierOrder")
    @ApiOperation("批量新增CourierOrder")
    public APIResponse batchCreateFaCourierOrder(@Validated @RequestBody List<FaCourierOrderEntity> resources) {
        Object o = faCourierOrderService.batchCreate(resources);
        return APIResponse.success(o);
    }

    @AnonymousPostMapping("shareCreate")
    @Log("分享新增CourierOrder")
    @ApiOperation("分享新增CourierOrder")
    public APIResponse shareCreateFaCourierOrder( @RequestBody FaCourierOrderEntity resources) {
        Integer id = faCourierOrderService.shareCreate( resources);
        return APIResponse.success(id);
    }

    @AnonymousGetMapping("sharelist")
    @Log("分享查询CourierOrder")
    @ApiOperation("分享查询CourierOrder")
    public APIResponse shareQueryFaCourierOrder(@ModelAttribute CourierOrderQuery query) {
        if (StringUtils.isEmpty(query.getOrderNo())){
            return APIResponse.success(new QueryResult<>());
        }
        String orderNoStr;
        try {
             orderNoStr = new String(Base64.getDecoder().decode(query.getOrderNo()));
        }catch (Exception e){
            log.error("",e.getMessage());
            return APIResponse.success(new QueryResult<>());
        }
        FaCourierOrderShareOrdernoEntity orderShareOrdernoEntity = faCourierOrderShareOrdernoDao.selectOne(new LambdaQueryWrapper<FaCourierOrderShareOrdernoEntity>().eq(FaCourierOrderShareOrdernoEntity::getOrderNo,orderNoStr));
        if (orderShareOrdernoEntity == null){
            return APIResponse.success(new QueryResult<>());
        }
        query.setOrderNo(orderNoStr);
        return APIResponse.success(faCourierOrderService.queryAll(query));
    }




    @GetMapping("getOrderNoCompanyIdUserId")
    @Log("getOrderNoCompanyIdUserId")
    @ApiOperation("分享链接url拼接订单号公司id用户id")
    public APIResponse getOrderNoCompanyIdUserId() {
        Map<String,String> map = faCourierOrderService.getOrderNoCompanyIdUserId();
        return APIResponse.success(map);
    }

    @PostMapping("getCourierFreightCharge")
    @Log("查询运费CourierOrder")
    @ApiOperation("查询运费CourierOrder")
    public APIResponse getCourierFreightCharge(@Validated @RequestBody FaCourierOrderEntity resources) {
        return APIResponse.success(faCourierOrderService.getCourierFreightCharge(resources,null));
    }
    @AnonymousGetMapping("getCourierCompanyFreightCharge")
    @Log("查询快递公司成本价运费CourierOrder")
    @ApiOperation("查询快递公司成本价运费CourierOrder")
    public APIResponse getCourierCompanyFreightCharge( @ApiParam(value = "id") @RequestParam() Integer id) {
        FaCourierOrderEntity order = faCourierOrderDao.selectById(id);
        return APIResponse.success(EstimatePriceUtil.getCourerCompanyCostPrice(order));
    }

    @ApiOperation("状态更新")
    @Log("状态更新")
    @GetMapping("updateState")
    public APIResponse cancelCourierOrder(@ApiParam(value = "uncancel:取消取消状态,cancel:取消状态，auditSuccess:审核成功，auditFail:审核失败") @RequestParam() String stateType,
                                          @ApiParam(value = "备注") @RequestParam(required = false) String content,
                                          @ApiParam(value = "id") @RequestParam() Integer id) {
        return APIResponse.success(faCourierOrderService.cancelCourierOrder(stateType,content,id));
    }

    @ApiOperation("订单删除")
    @Log("订单删除")
    @GetMapping("delete")
    public APIResponse delete(@ApiParam(value = "id") @RequestParam() Integer id) {
        faCourierOrderService.delete(id);
        return APIResponse.success();
    }
    @ApiOperation("根据订单号查询订单")
    @Log("根据订单号查询订单")
    @AnonymousGetMapping("selectOrderByOrderNo")
    public APIResponse selectOrderByOrderNo( @ApiParam(value = "orderNo") @RequestParam() String orderNo) {
        CourierOrderQuery query = new CourierOrderQuery();
        query.setOrderNo(orderNo);
        QueryResult<FaCourierOrderEntity>  queryResult = faCourierOrderService.queryAll(query);
        if (queryResult.getTotal() > 0){
            return APIResponse.success(queryResult.getItems().get(0));
        }
        return APIResponse.success();
    }

    @ApiOperation("根据订单号查询订单是否已付款")
    @Log("根据订单号查询订单是否已付款:true已经付款")
    @AnonymousGetMapping("selectOrderPayByOrderNo")
    public APIResponse selectOrderPayByOrderNo( @ApiParam(value = "orderNo") @RequestParam() String orderNo) {
        List<FaRechargeEntity> list = faRechargeDao.selectList(new LambdaQueryWrapper<FaRechargeEntity>().eq(FaRechargeEntity::getOrderid,orderNo).eq(FaRechargeEntity::getBusinessType,2));
        if (CollectionUtils.isNotEmpty(list)){
            return APIResponse.success(true);
        }
        return APIResponse.success(false);
    }

    @ApiOperation("更新收货时间")
    @Log("更新收货时间")
    @AnonymousPostMapping("updateGoodTime")
    public APIResponse updateGoodTime(
                                          @ApiParam(value = "takeGoodsTime") @RequestParam(required = false) String takeGoodsTime,
                                          @ApiParam(value = "takeGoodsTimeEnd") @RequestParam(required = false) String takeGoodsTimeEnd,
                                          @ApiParam(value = "id") @RequestParam() Integer id) {
        FaCourierOrderEntity order = new FaCourierOrderEntity();
        order.setId(id);
        order.setTakeGoodsTime(takeGoodsTime);
        order.setTakeGoodsTimeEnd(takeGoodsTimeEnd);
        faCourierOrderService.modifyOrder(order);
        return APIResponse.success();
    }

    @ApiOperation("根据id号查询订单")
    @Log("根据id号查询订单")
    @GetMapping("selectOrderById")
    public APIResponse selectOrderById(@ApiParam(value = "id") @RequestParam() Integer id) {
        CourierOrderQuery query = new CourierOrderQuery();
        query.setId(id);
        QueryResult<FaCourierOrderEntity>  queryResult = faCourierOrderService.queryAll(query);
        if (queryResult.getTotal() > 0){
            return APIResponse.success(queryResult.getItems().get(0));
        }
        return APIResponse.success();
    }

    @ApiOperation("地址识别")
    @AnonymousGetMapping("addressAnalysis")
    public APIResponse addressAnalysis( @ApiParam(value = "text") @RequestParam() String text) {
        return APIResponse.success(faCourierOrderService.addressAnalysis(text));
    }
    @ApiOperation("物流轨迹查询")
    @AnonymousGetMapping("queryCourierTrack")
    public APIResponse queryCourierTrack( @ApiParam(value = "id") @RequestParam() Integer id) {
        return APIResponse.success(faCourierOrderService.queryCourierTrack(id));
    }


    @ApiOperation("导出")
    @GetMapping(value = "/list/download")
    public void export(HttpServletResponse response, @ModelAttribute CourierOrderQuery query) throws IOException {
        query.setPageSize(10000);
        query.setPageNo(1);
        QueryResult<FaCourierOrderEntity> queryAll =  faCourierOrderService.queryAll(query);
        List<Map<String, Object>> list = new ArrayList<>();
        for (FaCourierOrderEntity e : queryAll.getItems()) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("账户名称", e.getCreateUserName());
            map.put("创建日期", DateUtil.format(e.getCreateTime(), "yyyy-MM-dd mm:hh:ss"));
            map.put("快递公司", e.getCourierCompanyCode());
            map.put("运单号", e.getCourierCompanyWaybillNo());
            map.put("物品名称", e.getGoodsName());
            map.put("预估重量", e.getWeight());
            map.put("预估费用", e.getEstimatePrice());//
            map.put("实际重量", e.getWeightto());
            map.put("实际费用", e.getPrice());
            String oname= "";
            if (e.getOrderState() == 0){
                oname = "待取件";
            }else if (e.getOrderState() == 1){
                oname = "调派";
            }else if (e.getOrderState() == 2){
                oname = "已取件";
            }else if (e.getOrderState() == 3){
                oname = "已取消";
            }
            map.put("订单状态", oname);

            //1运输中2派件中3已签收
            String wname= "";
            if (e.getWuliuState() != null){
                if (e.getWuliuState() == 1){
                    wname = "运输中";
                }else if (e.getWuliuState() == 2){
                    wname = "派件中";
                }else if (e.getWuliuState() == 3){
                    wname = "已签收";
                }else if (e.getWuliuState() == 4){
                    wname = "异常";
                }
            }

            map.put("物流状态", wname);

            //-1:待审核,0取消待审核1正常2审核通过3审核不通过
            String caname= "";
            if (e.getCancelOrderState() == -1){
                caname = "待审核";
            }else if (e.getCancelOrderState() == 0){
                caname = "取消待审核";
            }else if (e.getCancelOrderState() == 1){
                caname = "正常";
            }else if (e.getCancelOrderState() == 2){
                caname = "审核通过";
            }else if (e.getCancelOrderState() == 3){
                caname = "审核不通过";
            }
            map.put("订单取消状态", caname);
            map.put("取消原因", e.getReason());
            //0异常1正常
            map.put("是否异常", e.getOrderIsError() == 0 ? "异常" : "正常");
            String orderIsErrorMsg ="";
            if (StringUtils.isNotEmpty(e.getCourierOrderExtend())){
                JSONObject jsonObject = JSONObject.parseObject(e.getCourierOrderExtend(), JSONObject.class);
                if (jsonObject != null){
                    orderIsErrorMsg = jsonObject.getString("orderIsErrorMsg");;
                }
            }
            map.put("异常原因", orderIsErrorMsg);
            //0未结算1已结算
            map.put("是否结算", e.getIsJiesuan() == 1 ? "已结算" : "未结算");
            map.put("备注", e.getMsg());
            map.put("发货人", e.getFromName());
            map.put("发货手机号", e.getFromMobile());
            map.put("发出地址", e.getFromProv()+e.getFromCity()+e.getFromArea()+e.getFromAddress());
            map.put("收货人", e.getToName());
            map.put("收货手机号", e.getToMobile());
            map.put("收货地址", e.getToProv()+e.getToCity()+e.getToArea()+e.getToAddress());
            map.put("公司", e.getCompanyName());

//            map.put("宽度", e.getWidth());
//            map.put("长度", e.getLength());
//            map.put("高", e.getHeight());
//            map.put("期望上门取件时间", e.getTakeGoodsTime());
//            //1上门取件2门店自寄
//            map.put("寄件方式", e.getType() == 1 ? "上门取件" : "门店自寄");
//            //0待取件1已取件2已发出3已签收
//
//            //1自己取消2物流公司取消
//            map.put("取消类型", e.getCancelType() == 1 ? "自己取消" : "物流公司取消");
//            map.put("取消时间", DateUtil.format(e.getCancelTime(), "yyyy-MM-dd mm:hh:ss"));
//            map.put("取消原因", e.getReason());
//            map.put("取件时间", e.getQjTime());
//            map.put("签收时间", e.getQsTime());

            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}
