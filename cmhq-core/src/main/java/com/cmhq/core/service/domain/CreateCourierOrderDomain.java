package com.cmhq.core.service.domain;

import com.alibaba.fastjson.JSONObject;
import com.cmhq.core.api.UploadResult;
import com.cmhq.core.api.UploadTypeEnum;
import com.cmhq.core.api.dto.response.CourierFreightChargeDto;
import com.cmhq.core.api.strategy.StrategyFactory;
import com.cmhq.core.api.strategy.Upload;
import com.cmhq.core.dao.FaCompanyDao;
import com.cmhq.core.dao.FaCourierOrderExtDao;
import com.cmhq.core.dao.FaCourierOrderDao;
import com.cmhq.core.enums.MoneyConsumeEumn;
import com.cmhq.core.enums.MoneyConsumeMsgEumn;
import com.cmhq.core.model.CompanyMoneyParam;
import com.cmhq.core.model.FaCompanyEntity;
import com.cmhq.core.model.FaCourierOrderEntity;
import com.cmhq.core.service.FaCompanyMoneyService;
import com.cmhq.core.service.FaCourierOrderService;
import com.cmhq.core.util.CurrentUserContent;
import com.cmhq.core.util.SpringApplicationUtils;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.utils.SecurityUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
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
    private final FaCompanyMoneyService faCompanyMoneyService;

    public CreateCourierOrderDomain(FaCourierOrderEntity order) {

        if (order.getType() == null){
            order.setType(1);
        }
        order.setOrderState(0);
        order.setCancelOrderState(1);
        order.setOrderNo(System.currentTimeMillis() + "");
        order.setIsJiesuan(0);
        order.setOrderIsError(0);

        this.order = order;
        faCourierOrderDao = SpringApplicationUtils.getBean(FaCourierOrderDao.class);
        faCourierOrderExtDao = SpringApplicationUtils.getBean(FaCourierOrderExtDao.class);
        faCourierOrderService = SpringApplicationUtils.getBean(FaCourierOrderService.class);
        faCompanyDao = SpringApplicationUtils.getBean(FaCompanyDao.class);
        faCompanyMoneyService = SpringApplicationUtils.getBean(FaCompanyMoneyService.class);
    }

    public Integer handle(){
        try {
            Integer companyId = SecurityUtils.getCurrentCompanyId();
            if (companyId == null){
                throw new RuntimeException("当前用户非商户,无权限创建订单");
            }
            FaCompanyEntity faCompanyEntity = faCompanyDao.selectById(companyId);
            //获取价格

            //  预估金额 TODO 预估重量扣费
            double estimatePrice = getEstimatePrice(faCompanyEntity.getRatio());
            //账户余额不足
            if (faCompanyEntity.getMoney()  < estimatePrice){
                throw new RuntimeException("账户余额不足");
            }
            if(CurrentUserContent.isCompanyUser()){
            //子账户校验笔数、单笔、多笔最大金额
            }else if (CurrentUserContent.isCompanyChildUser()){
                if (CurrentUserContent.getCrrentUser().getChildUser() != null){
                    if ( estimatePrice > CurrentUserContent.getCrrentUser().getChildUser().getZiOneMaxMoney()){
                        throw new RuntimeException("子账户单笔金额达到上线");//TODO 其他参数校验
                    }
                    order.setZid(CurrentUserContent.getCrrentUser().getChildUser().getChildCompanyId());
                }
            }
            order.setFaCompanyId(companyId);
            order.setEstimatePrice(estimatePrice);
            order.setPrice(estimatePrice);
            order.setCourierOrderState(1);
            //下单
            faCourierOrderDao.insert(order);

            //额外字段插入
            saveOrderExt(order.getId(), order.getCourierOrderExtend());
            //插入消费记录
            faCompanyMoneyService.saveRecord(new CompanyMoneyParam(2, MoneyConsumeEumn.CONSUM_3, MoneyConsumeMsgEumn.MSG_3,estimatePrice,companyId, order.getId()+""));
            //上传物流信息到物流公司
            uploadCourierOrder();

        }catch (Exception e){
            log.error("",e);
            throw new RuntimeException(e.getMessage());
        }finally {
        }
        return order.getId();
    }

    private void uploadCourierOrder(){
        //上传物流公司
        Upload upload = StrategyFactory.getUpload(Objects.requireNonNull(UploadTypeEnum.getMsgByCode(order.getCourierCompanyCode(), UploadTypeEnum.TYPE_STO_ORDER_UPLOAD.getCodeNickName())));
        UploadResult uploadResult = upload.execute(order);
        if (uploadResult.getFlag()){
            JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(uploadResult.getJsonMsg()));
            FaCourierOrderEntity updateEntity = new FaCourierOrderEntity();
            updateEntity.setId(order.getId());
            if (StringUtils.isEmpty(jsonObject.getString("waybillNo"))){
                throw new RuntimeException("物流公司未返回运单号");
            }
            updateEntity.setCourierCompanyWaybillNo(jsonObject.getString("waybillNo"));
            updateEntity.setCourierCompanyOrderNo(jsonObject.getString("orderNo"));
            faCourierOrderDao.updateById(updateEntity);
        }else {
            throw new RuntimeException("物流公司上传失败:"+uploadResult.getErrorMsg());
        }
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
        Object obj =  faCourierOrderService.getCourierFreightCharge(order);
        if (obj instanceof CourierFreightChargeDto){
            CourierFreightChargeDto ccd = (CourierFreightChargeDto) obj;
            if (obj == null || CollectionUtils.isEmpty(ccd.getAvailableServiceItemList()) || ccd.getAvailableServiceItemList().get(0).getFeeModel() == null){
                throw new RuntimeException("获取预估价格失败");
            }

            CourierFreightChargeDto.FeeModel feeModel = ccd.getAvailableServiceItemList().get(0).getFeeModel();
            Integer totalPrice = Integer.parseInt(feeModel.getTotalPrice());
            log.info("获取运费价格 {}",JSONObject.toJSONString(feeModel));
            BigDecimal b = new BigDecimal((totalPrice * 100) * ((double) retio /100));
            double estimatePrice = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            //原始价格set
            order.setPriceto((double)totalPrice  * 100);

            //实际重量不知道 TODO
            // double totalInKilograms = (feeModel.getStartPrice()*100) +(feeModel.getStartWeight() / 500) * (feeModel.getContinuedHeavyPrice() ** 100);

            return estimatePrice;
        }
        throw new RuntimeException("未查到运费价格");
    }
}
