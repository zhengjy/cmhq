package com.cmhq.core.util;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cmhq.core.dao.FaCostDao;
import com.cmhq.core.model.FaCompanyCostEntity;
import com.cmhq.core.model.FaCostEntity;
import com.cmhq.core.model.dto.FreightChargeDto;
import com.cmhq.core.service.FaCompanyService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Jiyang.Zheng on 2024/4/23 14:03.
 */
@Slf4j
public class EstimatePriceUtil {


    /**
     * 通过物流系统价格表 计算实际价格
     * @param fromProv
     * @param toProv
     * @param weight
     * @param retio
     * @return
     */
    public static FreightChargeDto getPrice(String fromProv,String toProv,String fromCity, String toCity,Double weight,Integer retio){
        try {
            if (weight == null){
                weight =1D;
            }
            FaCostEntity costEntity = getCost(fromProv,toProv,fromCity,toCity);
            FreightChargeDto dto = new FreightChargeDto();
            dto.setTotalPriceInit(costEntity.getPrice());
            dto.setTotalPrice(doGetPrice(costEntity.getPrice(),costEntity.getPriceTo(), weight, retio));
            dto.setInfo(costEntity.getAddress()+"，1kg以内"+costEntity.getPrice()+"元，超出"+costEntity.getPriceTo()+"元/kg，不足1kg按1kg计算");
            return dto;


        }catch (Exception e){
            log.error("获取运费价格失败",e);
        }
        throw new RuntimeException("未查到运费价格");
    }

    private static FaCostEntity getCost(String fromProv,String toProv, String fromCity, String toCity){
        String toProv2 = "";
        if (StringUtils.equals(fromProv,toProv)){
            toProv2 = "省内";
            if (StringUtils.equals(fromCity,toCity)){
                toProv2 = "同城";
            }
        }

        FaCostDao faCostDao = SpringApplicationUtils.getBean(FaCostDao.class);
        List<FaCostEntity> list = faCostDao.selectList(new LambdaQueryWrapper<>());
        String finalToProv = toProv2;
        Optional<FaCostEntity> ocostEntity = list.stream().filter(v -> {
            if (fromProv.contains(v.getProF())
                    && (toProv.contains(v.getProD()) || (StringUtils.isNotEmpty(finalToProv) && (v.getProD().contains(finalToProv) || v.getProD().contains(toProv) )))){
                return true;
            }
            return false;
        }).findFirst();
        if (ocostEntity.isPresent()){
            return ocostEntity.get();
        }else {
            throw new RuntimeException("未匹配到省份报价费用");
        }
    }


    /**
     * 查询每个物流公司底价 得到最便宜的物流价格
     * @param fromProv
     * @param toProv
     * @param fromCity
     * @param toCity
     * @param weight
     * @param retio
     * @return
     */
    public static FreightChargeDto getCourerCompanyCostPrice(String fromProv, String toProv, String fromCity, String toCity, double weight, Integer retio){
        try {
            FaCompanyCostEntity costEntity = getCost2(fromProv,toProv,fromCity,toCity,null);
            FreightChargeDto dto = new FreightChargeDto();
            dto.setTotalPriceInit(costEntity.getPrice());
            dto.setCourierCompanyCode(costEntity.getCourierCompanyCode());
            dto.setTotalPrice(doGetPrice(costEntity.getPrice(),costEntity.getPriceTo(), weight, retio));
            return dto;
        }catch (Exception e){
            log.error("获取运费价格失败",e);
        }
        throw new RuntimeException("未查到运费价格");
    }

    /**
     * 查询每个物流公司底价 得到最便宜的物流价格  且排除某个物流公司
     * @param fromProv
     * @param toProv
     * @param fromCity
     * @param toCity
     * @param weight
     * @param retio
     * @param courierCompanyCode 物流 公司编码 多个 如：sto
     * @return
     */
    public static FreightChargeDto getCourerCompanyCostPriceAndFilterCourierCompanyCode(String fromProv, String toProv, String fromCity, String toCity, double weight, Integer retio, String courierCompanyCode){
        try {
            FaCompanyCostEntity costEntity = getCost2(fromProv,toProv,fromCity,toCity,courierCompanyCode);
            FreightChargeDto dto = new FreightChargeDto();
            dto.setTotalPriceInit(costEntity.getPrice());
            dto.setCourierCompanyCode(costEntity.getCourierCompanyCode());
            dto.setTotalPrice(doGetPrice(costEntity.getPrice(),costEntity.getPriceTo(), weight, retio));
            return dto;
        }catch (Exception e){
            log.error("获取运费价格失败",e);
        }
        throw new RuntimeException("未查到运费价格");
    }


    /**
     * 通过省、城市匹配，未匹配则通过省份匹配最大价格
     * @param fromProv
     * @param toProv
     * @param fromCity
     * @param toCity
     * @return
     */
    private static FaCompanyCostEntity getCost2(String fromProv,String toProv,String fromCity,String toCity,String courierCompanyCode){

        FaCompanyService faCostDao = SpringApplicationUtils.getBean(FaCompanyService.class);
        Environment enumeration = SpringApplicationUtils.getBean(Environment.class);
        String codes = enumeration.getProperty("filter.courier.code");

        List<FaCompanyCostEntity> list = faCostDao.selectFaCompanyCostList();
        Map<String,List<FaCompanyCostEntity>> listMap = list.stream().collect(Collectors.groupingBy(FaCompanyCostEntity::getCourierCompanyCode));
        FaCompanyCostEntity retCost = null;
        Set<String> keys = listMap.keySet();
        String toProv2 = "";
        if (StringUtils.equals(fromProv,toProv)){
            toProv2 = "省内";
            if (StringUtils.equals(fromCity,toCity)){
                toProv2 = "同城";
            }
        }
        for (String key : keys) {
            if (StringUtils.isNotEmpty(courierCompanyCode) && courierCompanyCode.contains(key)){
                continue;
            }
            if (!codes.contains(key)){
                continue;
            }
            List<FaCompanyCostEntity> value = listMap.get(key);
            FaCompanyCostEntity currCost = null;
            String finalToProv = toProv2;
            Optional<FaCompanyCostEntity> ocostEntity = value.stream().filter(v -> {
                //通过省匹配
                if (fromProv.contains(v.getProF())
                        && (toProv.contains(v.getProD()) || (StringUtils.isNotEmpty(finalToProv) && (v.getProD().contains(finalToProv) || v.getProD().contains(toProv) )))){
                    //通过城市匹配
                    if (StringUtils.isNotEmpty(v.getCityD())){
                        if (fromCity.equals("市辖区")){
                            if (toCity.contains(v.getCityD())){
                                return true;
                            }else {
                                return false;
                            }
                        }else {
                            if ( fromCity.contains(v.getCityF()) && toCity.contains(v.getCityD())){
                                return true;
                            }else {
                                return false;
                            }
                        }

                    }
                    return true;
                }
                return false;
            }).findFirst();
            //匹配到
            if (ocostEntity.isPresent()){
                currCost = ocostEntity.get();
            //未匹配到
            }else {
                //未匹配则通过省份匹配最大价格
                Optional<FaCompanyCostEntity> ocostEntity2 = value.stream()
                        .filter(v -> fromProv.contains(v.getProF()) && toProv.contains(v.getProD())).max(Comparator.comparing(FaCompanyCostEntity::getPrice));
                if (ocostEntity2.isPresent()){
                    currCost = ocostEntity2.get();
                }
            }
            if (currCost != null){
                if (retCost != null){
                    if (currCost.getPrice()  < retCost.getPrice()){
                        retCost = currCost;
                    }
                }else {
                    retCost = currCost;
                }
            }
        }
        if (retCost != null){
            return retCost;
        }else {
            throw new RuntimeException("未匹配到省份报价费用");
        }
    }

    private static double doGetPrice(Double costPrice,Double costPriceTo,double weight,Integer retio){
        //实际费用
        double price;
        if (weight > 1){
            double baseWeight = 1.0; // 首重
            double baseCost = costPrice; // 首重费用
            double additionalWeightCost = costPriceTo; // 续重费用
            double actualWeight = Math.ceil(weight); // 向上取整
            double cost = baseCost + (actualWeight - baseWeight) * additionalWeightCost; // 实际费用计算公式
            DecimalFormat df = new DecimalFormat("#.00"); // 格式化输出为两位小数
            price = Double.parseDouble(df.format(cost));
        }else {
            price = costPrice;
        }
        if (retio != null){
            BigDecimal b = new BigDecimal(price * ((double) retio /100));
            return b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        }
        return price;
    }


}
