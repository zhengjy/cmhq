package com.cmhq.core.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cmhq.core.dao.FaCompanyCostDao;
import com.cmhq.core.dao.FaProductDao;
import com.cmhq.core.model.FaCompanyCostEntity;
import com.cmhq.core.model.FaProductEntity;
import com.cmhq.core.model.param.ProductQuery;
import com.cmhq.core.util.SpringApplicationUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.APIResponse;
import me.zhengjie.QueryResult;
import me.zhengjie.annotation.AnonymousAccess;
import me.zhengjie.annotation.Log;
import me.zhengjie.annotation.rest.AnonymousGetMapping;
import me.zhengjie.utils.SecurityUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jiyang.Zheng on 2020/4/28 13:42.
 */
@RestController
@RequestMapping("product/")
@Api(value = "产品", tags = {"产品"})
@Slf4j

public class FaProductController {

    @Autowired
    private FaProductDao faProductDao;

    @ApiOperation("列表查询")
    @AnonymousGetMapping(value = "list")
    public APIResponse list(@ModelAttribute ProductQuery query) {
        PageHelper.startPage(query.getPageNo(), query.getPageSize());
        LambdaQueryWrapper<FaProductEntity> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotEmpty(query.getSearchTxt())){
            queryWrapper.like(FaProductEntity::getCategoryName,query.getSearchTxt());
        }
        if (query.getCompanyId() != null){
            queryWrapper.eq(FaProductEntity::getCid,query.getCompanyId());
        }else {
            if(SecurityUtils.getCurrentCompanyId() != null){
                queryWrapper.eq(FaProductEntity::getCid,SecurityUtils.getCurrentCompanyId());
            }
        }
        List<FaProductEntity> list = faProductDao.selectList(queryWrapper);
        PageInfo<FaProductEntity> page = new PageInfo<>(list);
        QueryResult<FaProductEntity> queryResult = new QueryResult<>();
        queryResult.setItems(list);
        queryResult.setTotal(page.getTotal());
        return APIResponse.success(queryResult);
    }
    @Log("修改新增")
    @AnonymousAccess
    @RequestMapping(value = "edit", method = RequestMethod.POST)
    public APIResponse edit( @RequestBody FaProductEntity entity) throws Exception {
        if (entity.getId() == null){
            entity.setCid(SecurityUtils.getCurrentCompanyId());
            faProductDao.insert(entity);
        }else {
            faProductDao.updateById(entity);
        }
        return APIResponse.success();
    }


    @ApiOperation("删除")
    @RequestMapping(value = "delete", method = RequestMethod.GET)
    public APIResponse delete( @ApiParam(value = "id") @RequestParam() Integer id) {
        faProductDao.deleteById(id);
        return APIResponse.success();
    }

    @Log("导入")
    @PostMapping(value = "uploadExcel",consumes = "multipart/form-data")
    public APIResponse uploadExcel(@RequestPart("file") MultipartFile file) {
        //1 获取文件输入流
        InputStream inputStream = null;
        try {
            inputStream = file.getInputStream();
            // 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
            EasyExcel.read(inputStream, FaProductEntity.class, new ProductDataListener()).headRowNumber(2).sheet().doRead();
        } catch (IOException e) {
            log.error("",e);
        }
        return APIResponse.success();
    }

}
@Slf4j
class ProductDataListener extends AnalysisEventListener<FaProductEntity> {
    /**
     * 每隔5条存储数据库，实际使用中可以3000条，然后清理list ，方便内存回收
     */
    private static final int BATCH_COUNT = 5;
    List<FaProductEntity> list = new ArrayList<>();
    int i = 1;


    /**
     * 这个每一条数据解析都会来调用
     *
     * @param data
     *            one row value. Is is same as {@link AnalysisContext#readRowHolder()}
     * @param context
     * 该方法不会读取表头
     * 一行一行读取excel内容
     */
    @Override
    public void invoke(FaProductEntity data, AnalysisContext context) {
        log.info("解析到一条数据:{}", JSONObject.toJSONString(data));//使用需要导入json依赖
        log.info("{} 数据，开始存储数据库！",i);
        FaProductDao dao = SpringApplicationUtils.getBean(FaProductDao.class);
        int num = dao.update(data,new LambdaQueryWrapper<FaProductEntity>()
                .eq(FaProductEntity::getCategoreCode,data.getCategoreCode())
        );
        if (num <= 0){
            dao.insert(data);
        }
        log.info("存储数据库成功！");
        i++;
    }
    /**
     * 所有数据解析完成了 都会来调用
     *读取完毕后操作
     * @param context
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // 这里也要保存数据，确保最后遗留的数据也存储到数据库
        saveData();
        log.info("所有数据解析完成！");
    }
    /**
     * 加上存储数据库
     */
    private void saveData() {

    }

}

