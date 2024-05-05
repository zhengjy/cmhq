package com.cmhq.core.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cmhq.core.dao.FaCompanyCostDao;
import com.cmhq.core.model.FaCompanyCostEntity;
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
@RequestMapping("fcCompanyCost/")
@Api(value = "快递公司-成本价", tags = {"快递公司-成本价"})
@Slf4j
public class FcCompanyCostController {

    @Autowired
    private FaCompanyCostDao faCompanyCostDao;

    @ApiOperation("列表查询")
    @RequestMapping(value = "list", method = RequestMethod.GET)
    public APIResponse list(@ModelAttribute CostQuery query) {
        PageHelper.startPage(query.getPageNo(), query.getPageSize());
        LambdaQueryWrapper<FaCompanyCostEntity> lam = new LambdaQueryWrapper<>();
        if (StringUtils.isNotEmpty(query.getFromPro())){
            lam.like(FaCompanyCostEntity::getProF,query.getFromPro());
        }
        if (StringUtils.isNotEmpty(query.getToPro())){
            lam.like(FaCompanyCostEntity::getProD,query.getToPro());
        }
        if (StringUtils.isNotEmpty(query.getToCity())){
            lam.like(FaCompanyCostEntity::getCityD,query.getFromPro());
        }
        if (StringUtils.isNotEmpty(query.getFromCity())){
            lam.like(FaCompanyCostEntity::getCityF,query.getToPro());
        }
        if (query.getSPrice() != null){
            lam.ge(FaCompanyCostEntity::getPrice, query.getSPrice())
                    .le(FaCompanyCostEntity::getPrice,query.getEPrice());
        }
        if (query.getSToPrice() != null){
            lam.ge(FaCompanyCostEntity::getPriceTo, query.getSToPrice())
                    .le(FaCompanyCostEntity::getPriceTo,query.getEToPrice());
        }
        List<FaCompanyCostEntity> list = faCompanyCostDao.selectList(lam);
        PageInfo<FaCompanyCostEntity> page = new PageInfo<>(list);
        QueryResult<FaCompanyCostEntity> queryResult = new QueryResult<>();
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
            EasyExcel.read(inputStream, FaCompanyCostEntity.class, new CostDataListener2()).headRowNumber(1).sheet().doRead();
        } catch (IOException e) {
            log.error("",e);
        }
        return APIResponse.success();
    }

}
@Slf4j
class CostDataListener2 extends AnalysisEventListener<FaCompanyCostEntity> {
    /**
     * 每隔5条存储数据库，实际使用中可以3000条，然后清理list ，方便内存回收
     */
    private static final int BATCH_COUNT = 5;
    List<FaCompanyCostEntity> list = new ArrayList<>();
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
    public void invoke(FaCompanyCostEntity data, AnalysisContext context) {
        log.info("解析到一条数据:{}", JSONObject.toJSONString(data));//使用需要导入json依赖
        log.info("{} 数据，开始存储数据库！",i);
        FaCompanyCostDao dao = SpringApplicationUtils.getBean(FaCompanyCostDao.class);
        int num = dao.update(data,new LambdaQueryWrapper<FaCompanyCostEntity>()
                .eq(FaCompanyCostEntity::getProD,data.getProD())
                .eq(FaCompanyCostEntity::getProF,data.getProF())
                .eq(FaCompanyCostEntity::getCityD,data.getCityD())
                .eq(FaCompanyCostEntity::getCityF,data.getCityF())
                .eq(FaCompanyCostEntity::getCourierCompanyCode,data.getCourierCompanyCode())
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


