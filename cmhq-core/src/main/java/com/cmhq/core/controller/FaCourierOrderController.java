package com.cmhq.core.controller;

import com.cmhq.core.model.param.CourierOrderQuery;
import com.cmhq.core.service.FaCourierOrderService;
import me.zhengjie.APIResponse;
import me.zhengjie.annotation.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
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
//
//    @PostMapping
//    @Log("新增CourierOrder")
//    @ApiOperation("新增CourierOrder")
//    @PreAuthorize("@el.check('faCourierOrder:add')")
//    public ResponseEntity<Object> createFaCourierOrder(@Validated @RequestBody FaCourierOrder resources) {
//        faCourierOrderService.create(resources);
//        return new ResponseEntity<>(HttpStatus.CREATED);
//    }
//
//    @PutMapping
//    @Log("修改CourierOrder")
//    @ApiOperation("修改CourierOrder")
//    @PreAuthorize("@el.check('faCourierOrder:edit')")
//    public ResponseEntity<Object> updateFaCourierOrder(@Validated @RequestBody FaCourierOrder resources) {
//        faCourierOrderService.update(resources);
//        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//    }
//
//    @DeleteMapping
//    @Log("删除CourierOrder")
//    @ApiOperation("删除CourierOrder")
//    @PreAuthorize("@el.check('faCourierOrder:del')")
//    public ResponseEntity<Object> deleteFaCourierOrder(@RequestBody Integer[] ids) {
//        faCourierOrderService.deleteAll(ids);
//        return new ResponseEntity<>(HttpStatus.OK);
//
//    }
//
//    @Log("导出数据")
//    @ApiOperation("导出数据")
//    @GetMapping(value = "/download")
//    @PreAuthorize("@el.check('faCourierOrder:list')")
//    public void exportFaCourierOrder(HttpServletResponse response, FaCourierOrderQueryCriteria criteria) throws IOException {
//        faCourierOrderService.download(faCourierOrderService.queryAll(criteria), response);
//    }
}
