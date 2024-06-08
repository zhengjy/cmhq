package com.cmhq.core.service.domain;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cmhq.core.api.dto.response.BaiduAddressAnlsDto;
import com.cmhq.core.dao.FaCourierOrderDao;
import com.cmhq.core.dao.FaProductDao;
import com.cmhq.core.model.FaCourierOrderEntity;
import com.cmhq.core.model.FaProductEntity;
import com.cmhq.core.model.param.CourierOrderImport;
import com.cmhq.core.service.FaCourierOrderService;
import com.cmhq.core.util.CurrentUserContent;
import com.cmhq.core.util.SpringApplicationUtils;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Jiyang.Zheng on 2024/6/6 18:57.
 */
@Slf4j
public class ImportCourierOrderDomain {
    private static FaProductDao faProductDao;
    private static FaCourierOrderDao faCourierOrderDao;
    private static FaCourierOrderService faCourierOrderService;
    private MultipartFile file;
    public ImportCourierOrderDomain(MultipartFile file){
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
            EasyExcel.read(inputStream, CourierOrderImport.class, new CourierImportListener()).headRowNumber(2).sheet().doRead();
        } catch (IOException e) {
            log.error("",e);
        }
        return null;
    }


    @Slf4j
    public static class CourierImportListener extends AnalysisEventListener<CourierOrderImport> {
        /**
         * 
         */
        Map<Integer,FaCourierOrderEntity> list = Maps.newLinkedHashMap();
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
        public void invoke(CourierOrderImport data, AnalysisContext context) {
            if (context.getCurrentRowNum() <=2){//是模板
                return;
            }
            count ++;
            int index = context.getCurrentRowNum()+1;
            log.info("解析到{}数据:{}",index, JSONObject.toJSONString(data));//使用需要导入json依赖
            FaCourierOrderEntity order = new FaCourierOrderEntity();
            JSONObject jo = new JSONObject();
            if (StringUtils.isNotEmpty(data.getGoodsType())){
                jo.put("goodsType","小件");
            }else {
                jo.put("goodsType","小件");
            }
            if (StringUtils.isNotEmpty(data.getJtGoodsType())){
                jo.put("jtGoodsType","其他");
            }else {
                jo.put("jtGoodsType","其他");
            }
            order.setCourierOrderExtend(jo.toJSONString());

            //参数校验
            if (StringUtils.isEmpty(data.getFromAddress())){
                fails.add("第"+index+"行,发货地不能为空");
                return;
            }
            if (StringUtils.isEmpty(data.getToAddress())){
                fails.add("第"+index+"行,收货地不能为空");
                return;
            }
            //产品名称编码查询
            if (StringUtils.isNotEmpty(data.getGoodsCode())){
                FaProductEntity faProductEntity = faProductDao.selectOne(new LambdaQueryWrapper<FaProductEntity>().eq(FaProductEntity::getCid, CurrentUserContent.getCurrentCompany().getId()).eq(FaProductEntity::getCategoreCode, data.getGoodsCode()).last(" limit 1"));
                if (faProductEntity != null){
                    order.setGoodsName(faProductEntity.getCategoryName());
                    order.setWeight(faProductEntity.getWeight());
                    order.setHeight(faProductEntity.getHeight());
                    order.setLength(faProductEntity.getLength());
                }else {
                    fails.add("第"+index+"行,商品编码不存在");
                    return;
                }
            }else {
                order.setGoodsName(data.getGoodsName());
                order.setWeight(data.getWeight());
                order.setHeight(data.getHeight());
                order.setLength(data.getLength());
            }

            //时间格式判断
            if (StringUtils.isNotEmpty(data.getTakeGoodsTime())){
                SimpleDateFormat ft2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    Date date = ft2.parse(data.getTakeGoodsTime());
                } catch (ParseException e) {
                    fails.add("第"+index+"行,开始时间格式错误，必须为yyyy-MM-dd HH:mm:ss");
                    return;
                }
                order.setTakeGoodsTime(data.getTakeGoodsTime());
            }
            if (StringUtils.isNotEmpty(data.getTakeGoodsTimeEnd())){
                SimpleDateFormat ft2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    Date date = ft2.parse(data.getTakeGoodsTimeEnd());
                } catch (ParseException e) {
                    fails.add("第"+index+"行,开始时间格式错误，必须为yyyy-MM-dd HH:mm:ss");
                    return;
                }
                order.setTakeGoodsTimeEnd(data.getTakeGoodsTimeEnd());
            }
            //地址解析
            try {
                Object o = faCourierOrderService.addressAnalysis(data.getFromAddress());
                if (StringUtils.isEmpty(o.toString())){
                    fails.add("第"+index+"行,收货地址未解析出省市地址");
                    return;
                }
                String s = o.toString();
//                s =s.replaceFirst("\"","");
//                s = s.substring(0,s.length()-1);
                BaiduAddressAnlsDto dto = JSONObject.parseObject(s, BaiduAddressAnlsDto.class);
                if (StringUtils.isEmpty(dto.getProvince())
                        || StringUtils.isEmpty(dto.getCity())
                        || StringUtils.isEmpty(dto.getDetail()) ){
                    fails.add("第"+index+"行,发货地址未解析出省市地址");
                    return;

                }
                if (StringUtils.isEmpty(dto.getPhonenum())
                        ){
                    fails.add("第"+index+"行,发货地址未解析出手机号码");
                    return;
                }
                if (StringUtils.isEmpty(dto.getPerson())
                ){
                    fails.add("第"+index+"行,发货地址未解析出发货人");
                    return;
                }
                order.setFromName(dto.getPerson());
                order.setFromMobile(dto.getPhonenum());
                order.setFromProv(dto.getProvince());
                order.setFromCity(dto.getCity());
                order.setFromArea(dto.getCounty());
                order.setFromAddress(dto.getDetail());
            }catch (Exception e){
                log.error("",e);
                fails.add("第"+index+"行,发货地址解析错误");
                return;
            }
            try {
                Object o = faCourierOrderService.addressAnalysis(data.getToAddress());
                if (StringUtils.isEmpty(o.toString())){
                    fails.add("第"+index+"行,收货地址未解析出省市地址");
                    return;
                }
                String s = o.toString();
//                s =s.replaceFirst("\"","");
//                s = s.substring(0,s.length()-1);
                BaiduAddressAnlsDto dto = JSONObject.parseObject(s, BaiduAddressAnlsDto.class);
                if (StringUtils.isEmpty(dto.getProvince())
                        || StringUtils.isEmpty(dto.getCity())
                        || StringUtils.isEmpty(dto.getDetail()) ){
                    fails.add("第"+index+"行,收货地址未解析出省市地址");
                    return;

                }
                if (StringUtils.isEmpty(dto.getPhonenum())){
                    fails.add("第"+index+"行,收货地址未解析出手机号码");
                    return;
                }
                if (StringUtils.isEmpty(dto.getPerson())
                ){
                    fails.add("第"+index+"行,收货地址未解析出发货人");
                    return;
                }
                order.setToName(dto.getPerson());
                order.setToMobile(dto.getPhonenum());
                order.setToProv(dto.getProvince());
                order.setToCity(dto.getCity());
                order.setToArea(dto.getCounty());
                order.setToAddress(dto.getDetail());
            }catch (Exception e){
                log.error("",e);
                fails.add("第"+index+"行,收货地址解析错误");
                return;
            }

            order.setFromPartitionNumber(data.getFromPartitionNumber());
            order.setToPartitionNumber(data.getToPartitionNumber());
            order.setMsg(data.getMsg());

            //调用创建订单接口
            list.put(index,order);
        }

        /**
         * 所有数据解析完成了 都会来调用
         * 读取完毕后操作
         *
         * @param context
         */
        @Override
        public void doAfterAllAnalysed(AnalysisContext context) {
            // 这里也要保存数据，确保最后遗留的数据也存储到数据库
            log.info("{}条数据，开始存储数据库！", list.size());
            list.forEach((k,v) ->{
                try {
                    faCourierOrderService.create(v);
                    Thread.sleep(1000);//太快京东会响应异常
                }catch (Exception e){
                    fails.add("第"+k+"行,订单上传物流公司异常，"+e.getMessage());
                    log.error("第{}行,订单上传物流公司异常 data={}",k,JSONObject.toJSONString(v));
                    log.error("第"+k+"行,订单上传物流公司异常",e);
                }
            });
            log.info("所有数据解析完成！");
            if (!fails.isEmpty()){
                StringBuilder sb = new StringBuilder();
                sb.append("上传总条数：").append(count).append("，上传成功数：").append(count - fails.size()).append("，上传失败数量：").append(fails.size());
                sb.append("\n 失败原因");
                sb.append(String.join("\n", fails));
                throw new RuntimeException(sb.toString());
            }
        }

    }

}

