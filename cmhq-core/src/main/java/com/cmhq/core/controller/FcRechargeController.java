package com.cmhq.core.controller;

import cn.hutool.core.date.DateUtil;
import com.cmhq.core.model.FaCompanyEntity;
import com.cmhq.core.model.FaRechargeEntity;
import com.cmhq.core.model.param.CompanyQuery;
import com.cmhq.core.model.param.FaRechargeQuery;
import com.cmhq.core.service.FaCompanyService;
import com.cmhq.core.service.FaRechargeService;
import com.cmhq.core.util.CurrentUserContent;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.APIResponse;
import me.zhengjie.QueryResult;
import me.zhengjie.annotation.Log;
import me.zhengjie.modules.system.domain.User;
import me.zhengjie.utils.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**商户充值记录
 * Created by Jiyang.Zheng on 2020/4/28 13:42.
 */
@RestController
@RequestMapping("fcRecharge/")
@Api(value = "商户充值记录", tags = {"商户充值记录"})
@Slf4j

public class FcRechargeController {

    @Autowired
    private FaRechargeService faRechargeService;


    @ApiOperation("列表查询")
    @RequestMapping(value = "list", method = RequestMethod.GET)
    public APIResponse list(@ModelAttribute FaRechargeQuery query) {
        return APIResponse.success(faRechargeService.list(query));
    }

    @Log("修改新增商户")
    @RequestMapping(value = "edit", method = RequestMethod.POST)
    public APIResponse edit( @RequestBody FaCompanyEntity entity) throws Exception {
        return APIResponse.success(faRechargeService.edit(entity));
    }


    @ApiOperation("删除商户")
    @RequestMapping(value = "delete", method = RequestMethod.GET)
    public APIResponse delete( @ApiParam(value = "id") @RequestParam() Integer id) {
        faRechargeService.delete(id);
        return APIResponse.success();
    }

    @ApiOperation("导出")
    @GetMapping(value = "/download")
    public void export(HttpServletResponse response, @ModelAttribute FaRechargeQuery query) throws IOException {
        query.setPageSize(10000);
        QueryResult<FaRechargeEntity> queryAll =  faRechargeService.list(query);
        List<Map<String, Object>> list = new ArrayList<>();
        for (FaRechargeEntity e : queryAll.getItems()) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("公司名字", e.getCompanyName());
            map.put("订单号", e.getOrderid());
            map.put("充值钱数", e.getMoney());
            map.put("是否支付", e.getStatus() == 1 ? "已支付" : "未支付");
            map.put("创建日期", DateUtil.format(e.getCreateTime(), "yyyy-MM-dd mm:hh:ss"));
            map.put("支付时间", DateUtil.format(e.getUpdateTime(), "yyyy-MM-dd mm:hh:ss"));
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

}

