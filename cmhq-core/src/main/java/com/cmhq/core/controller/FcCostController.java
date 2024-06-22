package com.cmhq.core.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cmhq.core.dao.FaCostDao;
import com.cmhq.core.model.FaCostEntity;
import com.cmhq.core.model.param.CostQuery;
import com.cmhq.core.util.SpringApplicationUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.APIResponse;
import me.zhengjie.QueryResult;
import me.zhengjie.annotation.Log;
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
@RequestMapping("fcCost/")
@Api(value = "物流报价", tags = {"物流报价"})
@Slf4j
public class FcCostController {

    @Autowired
    private FaCostDao faCostDao;

    @ApiOperation("列表查询")
    @RequestMapping(value = "list", method = RequestMethod.GET)
    public APIResponse list(@ModelAttribute CostQuery query) {
        PageHelper.startPage(query.getPageNo(), query.getPageSize());
        LambdaQueryWrapper<FaCostEntity> lam = new LambdaQueryWrapper<>();
        if (StringUtils.isNotEmpty(query.getFromPro())){
            lam.like(FaCostEntity::getProF,query.getFromPro());
        }
        if (StringUtils.isNotEmpty(query.getToPro())){
            lam.like(FaCostEntity::getProD,query.getToPro());
        }
        if (query.getSPrice() != null){
            lam.ge(FaCostEntity::getPrice, query.getSPrice())
                    .le(FaCostEntity::getPrice,query.getEPrice());
        }
        if (query.getSToPrice() != null){
            lam.ge(FaCostEntity::getPriceTo, query.getSToPrice())
                    .le(FaCostEntity::getPriceTo,query.getEToPrice());
        }
        List<FaCostEntity> list = faCostDao.selectList(lam);
        PageInfo<FaCostEntity> page = new PageInfo<>(list);
        QueryResult<FaCostEntity> queryResult = new QueryResult<>();
        queryResult.setItems(list);
        queryResult.setTotal(page.getTotal());
        return APIResponse.success(queryResult);
    }
    @Log("导入")
    @PostMapping(value = "uploadExcel",consumes = "multipart/form-data")
    public APIResponse uploadExcel(@RequestPart("file") MultipartFile file) {
        //1 获取文件输入流
        InputStream inputStream = null;
        try {
            inputStream = file.getInputStream();
            // 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
            EasyExcel.read(inputStream, FaCostEntity.class, new CostDataListener()).headRowNumber(1).sheet().doRead();
        } catch (IOException e) {
            log.error("",e);
        }
        return APIResponse.success();
    }

}
@Slf4j
class CostDataListener extends AnalysisEventListener<FaCostEntity> {
    /**
     * 每隔5条存储数据库，实际使用中可以3000条，然后清理list ，方便内存回收
     */
    private static final int BATCH_COUNT = 5;
    List<FaCostEntity> list = new ArrayList<>();


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
    public void invoke(FaCostEntity data, AnalysisContext context) {
        log.info("解析到一条数据:{}", JSONObject.toJSONString(data));//使用需要导入json依赖
        list.add(data);
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
        log.info("{}条数据，开始存储数据库！", list.size());
        FaCostDao dao = SpringApplicationUtils.getBean(FaCostDao.class);
        list.forEach(v ->{
            int num = dao.update(v,new LambdaQueryWrapper<FaCostEntity>().eq(FaCostEntity::getProD,v.getProD()).eq(FaCostEntity::getProF,v.getProF()).eq(FaCostEntity::getAddress,v.getAddress()));
            if (num <= 0){
                dao.insert(v);
            }else {
                log.info("已存在【{}】",JSONObject.toJSONString(v));
            }
        });
        log.info("存储数据库成功！");
    }
}


