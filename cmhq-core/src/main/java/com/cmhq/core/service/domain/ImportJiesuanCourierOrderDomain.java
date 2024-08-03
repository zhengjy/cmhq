package com.cmhq.core.service.domain;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cmhq.core.api.dto.response.ActualFeeInfoDto;
import com.cmhq.core.dao.FaCourierOrderDao;
import com.cmhq.core.dao.FaProductDao;
import com.cmhq.core.model.FaCourierOrderEntity;
import com.cmhq.core.model.param.CourierJiesuanOrderImport;
import com.cmhq.core.service.FaCourierOrderService;
import com.cmhq.core.util.SpringApplicationUtils;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Jiyang.Zheng on 2024/6/6 18:57.
 */
@Slf4j
public class ImportJiesuanCourierOrderDomain {
    private static FaProductDao faProductDao;
    private static FaCourierOrderDao faCourierOrderDao;
    private static FaCourierOrderService faCourierOrderService;
    private MultipartFile file;
    public ImportJiesuanCourierOrderDomain(MultipartFile file){
        faProductDao = SpringApplicationUtils.getBean(FaProductDao.class);
        faCourierOrderDao = SpringApplicationUtils.getBean(FaCourierOrderDao.class);
        faCourierOrderService = SpringApplicationUtils.getBean(FaCourierOrderService.class);
        this.file = file;
    }

    public Object handle(){
        //1 获取文件输入流
        InputStream inputStream = null;
        try {
            inputStream = file.getInputStream();
            // 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
            EasyExcel.read(inputStream, CourierJiesuanOrderImport.class, new CourierImportListener()).headRowNumber(1).sheet().doRead();
        } catch (IOException e) {
            log.error("",e);
        }
        return null;
    }


    @Slf4j
    public static class CourierImportListener extends AnalysisEventListener<CourierJiesuanOrderImport> {
        /**
         * 
         */
        List<CourierJiesuanOrderImport> list = Lists.newArrayList();
        List<String> fails = new ArrayList<>();
        int count =0;


        /**
         * 这个每一条数据解析都会来调用
         *
         * @param data    one row value. Is is same as {@link AnalysisContext#readRowHolder()}
         * @param context 该方法不会读取表头
         *                一行一行读取excel内容
         */
        @Override
        public void invoke(CourierJiesuanOrderImport data, AnalysisContext context) {
            count ++;
            int index = context.getCurrentRowNum()+1;
            log.info("解析到{}数据:{}",index, JSONObject.toJSONString(data));//使用需要导入json依赖
            list.add(data);
        }

        /**
         * 所有数据解析完成了 都会来调用
         * 读取完毕后操作
         *
         * @param context
         */
        @Override
        public void doAfterAllAnalysed(AnalysisContext context) {
            log.info("{}条数据，！", list.size());
            Map<String,List<CourierJiesuanOrderImport>> map  = list.stream().collect(Collectors.groupingBy(CourierJiesuanOrderImport::getCourierCompanyWaybillNo));
            map.forEach((k,v) ->{
                LambdaQueryWrapper<FaCourierOrderEntity> lam = new LambdaQueryWrapper<>();
                lam.eq(FaCourierOrderEntity::getCourierCompanyWaybillNo, k);
                lam.last(" limit 1");
                FaCourierOrderEntity order = faCourierOrderDao.selectOne(lam);
                if (order != null){
                    Optional<CourierJiesuanOrderImport> jiesuanOrder = v.stream().filter(o -> o.getFeeType().equals(CourierJiesuanOrderImport.FEE_TYPE_KDYF)).findFirst();
                    if (jiesuanOrder.isPresent()){
                        List<ActualFeeInfoDto> list1 = v.stream()
                                .filter(o -> o.getFeeType().equals(CourierJiesuanOrderImport.FEE_TYPE_KDCCCC)
                                        || o.getFeeType().equals(CourierJiesuanOrderImport.FEE_TYPE_KDBZF))
                                .map(f -> {
                                    ActualFeeInfoDto dto = new ActualFeeInfoDto();
                                    if (f.getFeeType().equals(CourierJiesuanOrderImport.FEE_TYPE_KDCCCC)){
                                        dto.setFeeType(ActualFeeInfoDto.FEETYPE_CCCC);
                                    }else if (f.getFeeType().equals(CourierJiesuanOrderImport.FEE_TYPE_KDBZF)){
                                        dto.setFeeType(ActualFeeInfoDto.FEETYPE_QLHCF);
                                    }
                                    dto.setMoney(f.getJiesuanMoney());
                                    return dto;
                                }).collect(Collectors.toList());
                        new OrderJiesuanAfterCheckWeightDomain(order,jiesuanOrder.get().getWeight(),list1 ).handle();

                    }
                }


            });

        }

    }

}

