package com.cmhq.core.service.domain;

import com.alibaba.fastjson.JSONObject;
import com.cmhq.core.api.UploadResult;
import com.cmhq.core.api.UploadTypeEnum;
import com.cmhq.core.api.strategy.StrategyFactory;
import com.cmhq.core.api.strategy.Upload;
import com.cmhq.core.dao.FaCompanyDao;
import com.cmhq.core.dao.FaCourierOrderExtDao;
import com.cmhq.core.dao.FaCourierOrderDao;
import com.cmhq.core.enums.MoneyConsumeEumn;
import com.cmhq.core.enums.MoneyConsumeMsgEumn;
import com.cmhq.core.model.CompanyMoneyParam;
import com.cmhq.core.model.FaCompanyDayOpeNumEntity;
import com.cmhq.core.model.FaCompanyEntity;
import com.cmhq.core.model.FaCourierOrderEntity;
import com.cmhq.core.model.dto.CreateCourierOrderResponseDto;
import com.cmhq.core.model.dto.FreightChargeDto;
import com.cmhq.core.service.FaCompanyDayOpeNumService;
import com.cmhq.core.service.FaCompanyMoneyService;
import com.cmhq.core.service.FaCompanyService;
import com.cmhq.core.service.FaCourierOrderService;
import com.cmhq.core.util.CurrentUserContent;
import com.cmhq.core.util.SpringApplicationUtils;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.utils.SecurityUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Created by Jiyang.Zheng on 2024/4/10 15:18.
 */
@Slf4j
public class CreateCourierOrderDomain {

    private FaCourierOrderEntity order;
    private final FaCourierOrderDao faCourierOrderDao;
    private final FaCourierOrderExtDao faCourierOrderExtDao;
    private final FaCourierOrderService faCourierOrderService;
    private final FaCompanyDao faCompanyDao;
    private final FaCompanyService faCompanyService;
    private final FaCompanyMoneyService faCompanyMoneyService;
    private final FaCompanyDayOpeNumService faCompanyDayOpeNumService;

    public CreateCourierOrderDomain(FaCourierOrderEntity order) {

        if (order.getType() == null){
            order.setType(1);
        }
        order.setOrderState(0);
        order.setCancelOrderState(1);
        order.setOrderNo(System.currentTimeMillis() + "");
        order.setIsJiesuan(0);
        order.setOrderIsError(1);
        order.setCreateUserId(SecurityUtils.getCurrentUserId().intValue());

        this.order = order;
        faCourierOrderDao = SpringApplicationUtils.getBean(FaCourierOrderDao.class);
        faCourierOrderExtDao = SpringApplicationUtils.getBean(FaCourierOrderExtDao.class);
        faCourierOrderService = SpringApplicationUtils.getBean(FaCourierOrderService.class);
        faCompanyDao = SpringApplicationUtils.getBean(FaCompanyDao.class);
        faCompanyService = SpringApplicationUtils.getBean(FaCompanyService.class);
        faCompanyMoneyService = SpringApplicationUtils.getBean(FaCompanyMoneyService.class);
        faCompanyDayOpeNumService = SpringApplicationUtils.getBean(FaCompanyDayOpeNumService.class);
    }

    public Integer handle(){
        double estimatePrice = 0;
        try {
            Integer companyId = SecurityUtils.getCurrentCompanyId();
            if (companyId == null){
                throw new RuntimeException("当前用户非商户,无权限创建订单");
            }
            FaCompanyEntity faCompanyEntity = faCompanyService.selectById(companyId);
            //  预估金额
            estimatePrice = getEstimatePrice(faCompanyEntity.getRatio());

            check(estimatePrice,faCompanyEntity);
            order.setFaCompanyId(companyId);
            order.setEstimatePrice(estimatePrice);
            order.setPrice(estimatePrice);
            order.setCourierOrderState(1);
            //下单
            faCourierOrderDao.insert(order);
            //额外字段插入
            saveOrderExt(order.getId(), order.getCourierOrderExtend());
            //上传物流信息到物流公司
            String billNo = uploadCourierOrder();
            //插入消费记录
            faCompanyMoneyService.saveRecord(new CompanyMoneyParam(2, MoneyConsumeEumn.CONSUM_3, MoneyConsumeMsgEumn.MSG_3,estimatePrice,companyId, order.getId()+"",billNo));
        }catch (Exception e){
            log.error("",e);
            throw new RuntimeException(e.getMessage());
        }finally {
            //记录统计量
            faCompanyDayOpeNumService.updateValue(LocalDate.now().toString(),FaCompanyDayOpeNumEntity.TYPE_ORDER_CREATE_NUM,1);
            faCompanyDayOpeNumService.updateValue(LocalDate.now().toString(),FaCompanyDayOpeNumEntity.TYPE_ORDER_CREATE_MAX_MONEY,estimatePrice);

        }
        return order.getId();
    }

    private void check(double estimatePrice,FaCompanyEntity faCompanyEntity){
        if (StringUtils.equals(faCompanyEntity.getIsFreeze(),"Y")){
            throw new RuntimeException("账号已冻结，请联系运营人员；");
        }

        //账户余额不足
        if (faCompanyEntity.getMoney()  < estimatePrice){
            throw new RuntimeException("账户余额不足");
        }

        if (faCompanyEntity.getZiOneMaxMoney() != null && faCompanyEntity.getZiOneMaxMoney()  > 0 && estimatePrice > faCompanyEntity.getZiOneMaxMoney()){
            throw new RuntimeException("商户账户单笔订单最大限制金额");
        }

        if (faCompanyEntity.getZiMaxMoney() != null && faCompanyEntity.getZiMaxMoney() > 0){
            //商户日最大金额
            double createOrderMaxMoney = faCompanyDayOpeNumService.selectValue(LocalDate.now().toString(),LocalDate.now().toString(), FaCompanyDayOpeNumEntity.TYPE_ORDER_CREATE_MAX_MONEY);
            if (createOrderMaxMoney >= faCompanyEntity.getZiMaxMoney()){
                throw new RuntimeException("商户当日最大金额已达上限");
            }
        }

        if (faCompanyEntity.getZiMaxNum() != null && faCompanyEntity.getZiMaxNum() > 0){
            //商户日下单量
            double createOrderNum = faCompanyDayOpeNumService.selectValue(LocalDate.now().toString(),LocalDate.now().toString(), FaCompanyDayOpeNumEntity.TYPE_ORDER_CREATE_NUM);
            if (createOrderNum >= faCompanyEntity.getZiMaxNum()){
                throw new RuntimeException("商户当日下单量已达上限");
            }
        }

        if (CurrentUserContent.isCompanyChildUser() && CurrentUserContent.getCrrentUser().getChildUser() != null){
            if ( estimatePrice > CurrentUserContent.getCrrentUser().getChildUser().getZiOneMaxMoney()){
                throw new RuntimeException("账户单笔金额达到上线");
            }
            if (CurrentUserContent.getCrrentUser().getChildUser().getZiMaxMoney() != null){
                //商户日最大金额
                double createOrderMaxMoney = faCompanyDayOpeNumService.selectValue(LocalDate.now().toString(),LocalDate.now().toString(), FaCompanyDayOpeNumEntity.TYPE_ORDER_CREATE_MAX_MONEY);
                if (createOrderMaxMoney >= CurrentUserContent.getCrrentUser().getChildUser().getZiMaxMoney()){
                    throw new RuntimeException("账户当日最大金额已达上限");
                }
            }
            if (CurrentUserContent.getCrrentUser().getChildUser().getZiMaxNum() != null){
                //商户日下单量
                double createOrderNum = faCompanyDayOpeNumService.selectValue(LocalDate.now().toString(),LocalDate.now().toString(), FaCompanyDayOpeNumEntity.TYPE_ORDER_CREATE_NUM);
                if (createOrderNum >= CurrentUserContent.getCrrentUser().getChildUser().getZiMaxNum()){
                    throw new RuntimeException("账户当日下单量已达上限");
                }
            }
            order.setZid(CurrentUserContent.getCrrentUser().getChildUser().getChildCompanyId());
        }
    }

    private String uploadCourierOrder(){
        String billNo = "";
        //上传物流公司
        Upload upload = StrategyFactory.getUpload(Objects.requireNonNull(UploadTypeEnum.getMsgByCode(order.getCourierCompanyCode(), UploadTypeEnum.TYPE_STO_ORDER_UPLOAD.getCodeNickName())));
        UploadResult uploadResult = upload.execute(order);
        if (uploadResult.getFlag()){
            if (uploadResult.getJsonMsg() != null && uploadResult.getJsonMsg() instanceof CreateCourierOrderResponseDto){
                CreateCourierOrderResponseDto rsp = (CreateCourierOrderResponseDto) uploadResult.getJsonMsg();
                FaCourierOrderEntity updateEntity = new FaCourierOrderEntity();
                updateEntity.setId(order.getId());
                billNo = rsp.getWaybillNo();
                updateEntity.setCourierCompanyWaybillNo(billNo);
                faCourierOrderDao.updateById(updateEntity);
            }else {
                throw new RuntimeException("物流公司未返回运单号");
            }
        }else {
            throw new RuntimeException("物流公司上传失败:"+uploadResult.getErrorMsg());
        }
        return billNo;
    }

    private void saveOrderExt(Integer id,String ext){
        JSONObject jo = JSONObject.parseObject(ext);
        if (jo != null){
            for (String key : jo.keySet()) {
                faCourierOrderService.saveOrderExt(id,key,jo.getString(key));
            }
        }
    }

    /**
     * 不同订单公司运算不同
     * 首重费用+【实际重量（四舍五入取整）-1】*续重费用=实际费用
     * 实际重量【按实际重量与体积重量（计泡重量）两者取最大值计算运费】
     * 获取预估价格
     * @return
     */
    private double getEstimatePrice(Integer retio){
        FreightChargeDto dto =  faCourierOrderService.getCourierFreightCharge(order);
        try {
            if (dto != null && dto.getTotalPrice() != null){
                log.info("获取运费价格 {}",JSONObject.toJSONString(dto));
                BigDecimal b = new BigDecimal(dto.getTotalPrice() * ((double) retio /100));
                double estimatePrice = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                //原始价格set
                order.setPriceto(dto.getTotalPrice() );
                order.setCourierCompanyCode(dto.getCourierCompanyCode());
                // double totalInKilograms = (feeModel.getStartPrice()*100) +(feeModel.getStartWeight() / 500) * (feeModel.getContinuedHeavyPrice() ** 100);
                return estimatePrice;
            }
        }catch (Exception e){
            log.error("获取运费价格失败",e);
        }
        throw new RuntimeException("未查到运费价格");
    }
}
