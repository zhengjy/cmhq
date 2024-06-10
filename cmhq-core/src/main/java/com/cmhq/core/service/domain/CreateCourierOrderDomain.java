package com.cmhq.core.service.domain;

import com.alibaba.fastjson.JSONObject;
import com.cmhq.core.api.UploadResult;
import com.cmhq.core.api.UploadTypeEnum;
import com.cmhq.core.api.strategy.StrategyFactory;
import com.cmhq.core.api.strategy.Upload;
import com.cmhq.core.dao.FaCourierOrderDao;
import com.cmhq.core.enums.MoneyConsumeEumn;
import com.cmhq.core.enums.MoneyConsumeMsgEumn;
import com.cmhq.core.model.param.CompanyMoneyParam;
import com.cmhq.core.model.FaCompanyDayOpeNumEntity;
import com.cmhq.core.model.FaCompanyEntity;
import com.cmhq.core.model.FaCourierOrderEntity;
import com.cmhq.core.model.dto.CreateCourierOrderResponseDto;
import com.cmhq.core.model.dto.FreightChargeDto;
import com.cmhq.core.service.FaCompanyDayOpeNumService;
import com.cmhq.core.service.FaCompanyMoneyService;
import com.cmhq.core.service.FaCompanyService;
import com.cmhq.core.service.FaCourierOrderService;
import com.cmhq.core.util.EstimatePriceUtil;
import com.cmhq.core.util.SpringApplicationUtils;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.modules.system.service.UserService;
import me.zhengjie.modules.system.service.dto.UserDto;
import me.zhengjie.utils.SecurityUtils;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Created by Jiyang.Zheng on 2024/4/10 15:18.
 */
@Slf4j
public class CreateCourierOrderDomain {

    private final FaCourierOrderEntity order;
    private final FaCourierOrderDao faCourierOrderDao;
    private final FaCourierOrderService faCourierOrderService;
    private final FaCompanyService faCompanyService;
    private final FaCompanyMoneyService faCompanyMoneyService;
    private final FaCompanyDayOpeNumService faCompanyDayOpeNumService;

    public CreateCourierOrderDomain(FaCourierOrderEntity order,Integer companyId,Long userId,String orderNo){
        if (order.getType() == null){
            order.setType(1);
        }
        if (order.getWeight() == null){
            order.setWeight(1D);
        }
        if (StringUtils.isEmpty(order.getGoodsName())){
            order.setGoodsName("灯具");
        }
        if (StringUtils.isNotEmpty(order.getTakeGoodsTime()) && order.getTakeGoodsTime().length() == 16){
            order.setTakeGoodsTime(order.getTakeGoodsTime() +":00");
        }
        if (StringUtils.isNotEmpty(order.getTakeGoodsTimeEnd()) && order.getTakeGoodsTimeEnd().length() == 16){
            order.setTakeGoodsTimeEnd(order.getTakeGoodsTimeEnd() +":00");
        }
        order.setOrderState(0);
        order.setCourierOrderState("");
        order.setCourierWuliuState("");
        order.setCancelOrderState(1);
        order.setOrderNo(orderNo);
        order.setIsJiesuan(0);
        order.setOrderIsError(1);
        order.setCreateUserId(userId.intValue());
        order.setFaCompanyId(companyId);
        //获取物流公司底价选择最便宜的价格快递公司
        if (StringUtils.isEmpty(order.getCourierCompanyCode())){
            FreightChargeDto dto =  EstimatePriceUtil.getCourerCompanyCostPrice(order.getFromProv(),order.getToProv(),order.getFromCity(),order.getToCity(),order.getWeight(),null);
            order.setCourierCompanyCode(dto.getCourierCompanyCode());
        }
        this.order = order;
        faCourierOrderDao = SpringApplicationUtils.getBean(FaCourierOrderDao.class);
        faCourierOrderService = SpringApplicationUtils.getBean(FaCourierOrderService.class);
        faCompanyService = SpringApplicationUtils.getBean(FaCompanyService.class);
        faCompanyMoneyService = SpringApplicationUtils.getBean(FaCompanyMoneyService.class);
        faCompanyDayOpeNumService = SpringApplicationUtils.getBean(FaCompanyDayOpeNumService.class);
    }




    public CreateCourierOrderDomain(FaCourierOrderEntity order) {
        this(order,SecurityUtils.getCurrentCompanyId(),SecurityUtils.getCurrentUserId(),System.currentTimeMillis() + "");
    }

    public Integer handle(){
        double price = 0;
        Integer companyId = order.getFaCompanyId();
        try {
            if (companyId == null){
                throw new RuntimeException("当前用户非商户,无权限创建订单");
            }
            FaCompanyEntity faCompanyEntity = faCompanyService.selectById(companyId);
            //  实际费用
            price = getPrice(faCompanyEntity.getRatio());

            check(price,faCompanyEntity);
            order.setEstimatePrice(price);
            order.setPrice(0D);
            if (order.getType() ==null){
                order.setType(1);
            }
            order.setCancelOrderState(1);
            order.setIsJiesuan(0);
            //下单
            faCourierOrderDao.insert(order);
            //额外字段插入
            saveOrderExt(order.getId(), order.getCourierOrderExtend());
            //上传订单信息到物流公司
            String billNo = uploadCourierOrder();
            //插入消费记录
            faCompanyMoneyService.saveRecord(new CompanyMoneyParam(2, MoneyConsumeEumn.CONSUM_3, MoneyConsumeMsgEumn.MSG_3,price,companyId, order.getId()+"",billNo));
            //记录统计量
            faCompanyDayOpeNumService.updateValue(LocalDate.now().toString(),FaCompanyDayOpeNumEntity.TYPE_ORDER_CREATE_NUM,1,companyId);
            faCompanyDayOpeNumService.updateValue(LocalDate.now().toString(),FaCompanyDayOpeNumEntity.TYPE_ORDER_CREATE_MAX_MONEY,price,companyId);
        }catch (Exception e){
            log.error("",e);
            throw new RuntimeException(e.getMessage());
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
            double createOrderMaxMoney = faCompanyDayOpeNumService.selectValue(LocalDate.now().toString(),LocalDate.now().toString(), FaCompanyDayOpeNumEntity.TYPE_ORDER_CREATE_MAX_MONEY,faCompanyEntity.getId().longValue());
            if (createOrderMaxMoney >= faCompanyEntity.getZiMaxMoney()){
                throw new RuntimeException("商户当日最大金额已达上限");
            }
        }

        if (faCompanyEntity.getZiMaxNum() != null && faCompanyEntity.getZiMaxNum() > 0){
            //商户日下单量
            double createOrderNum = faCompanyDayOpeNumService.selectValue(LocalDate.now().toString(),LocalDate.now().toString(), FaCompanyDayOpeNumEntity.TYPE_ORDER_CREATE_NUM,faCompanyEntity.getId().longValue());
            if (createOrderNum >= faCompanyEntity.getZiMaxNum()){
                throw new RuntimeException("商户当日下单量已达上限");
            }
        }

        UserService userService = SpringApplicationUtils.getBean(UserService.class);
        long userId = order.getCreateUserId().longValue();
        UserDto userDto = userService.findById(userId);
        if (userDto.getChildUser() != null){
            if ( estimatePrice > userDto.getChildUser().getZiOneMaxMoney()){
                throw new RuntimeException("账户单笔金额达到上线");
            }
            if (userDto.getChildUser().getZiMaxMoney() != null){
                //商户日最大金额
                double createOrderMaxMoney = faCompanyDayOpeNumService.selectValue(LocalDate.now().toString(),LocalDate.now().toString(), FaCompanyDayOpeNumEntity.TYPE_ORDER_CREATE_MAX_MONEY,faCompanyEntity.getId().longValue());
                if (createOrderMaxMoney >= userDto.getChildUser().getZiMaxMoney()){
                    throw new RuntimeException("账户当日最大金额已达上限");
                }
            }
            if (userDto.getChildUser().getZiMaxNum() != null){
                //商户日下单量
                double createOrderNum = faCompanyDayOpeNumService.selectValue(LocalDate.now().toString(),LocalDate.now().toString(), FaCompanyDayOpeNumEntity.TYPE_ORDER_CREATE_NUM,faCompanyEntity.getId().longValue());
                if (createOrderNum >= userDto.getChildUser().getZiMaxNum()){
                    throw new RuntimeException("账户当日下单量已达上限");
                }
            }
            order.setZid(userDto.getChildUser().getChildCompanyId());
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
                updateEntity.setCourierCompanyOrderNo(rsp.getOrderNo());
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
    private double getPrice(Integer retio){
        return faCourierOrderService.getCourierFreightCharge(order,retio).getTotalPrice();

    }
}
