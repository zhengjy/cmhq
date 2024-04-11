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
import com.cmhq.core.model.FaCompanyEntity;
import com.cmhq.core.model.FaCourierOrderEntity;
import com.cmhq.core.service.FaCompanyMoneyService;
import com.cmhq.core.service.FaCourierOrderService;
import com.cmhq.core.util.CurrentUserContent;
import com.cmhq.core.util.SpringApplicationUtils;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.utils.SecurityUtils;
import org.apache.commons.lang3.StringUtils;

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
            FaCompanyEntity faCompanyEntity = faCompanyDao.selectById(companyId);
            //  预估金额 TODO 预估重量扣费
            double estimatePrice = order.getPrice() * ((double) faCompanyEntity.getRatio() /100);
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
            order.setEstimatePrice(estimatePrice);
            //下单
            faCourierOrderDao.insert(order);

            //额外字段插入
            saveOrderExt(order.getId(), order.getCourierOrderExtend());
            //插入消费记录
            faCompanyMoneyService.saveRecord(new CompanyMoneyParam(2, MoneyConsumeEumn.CONSUM_3, MoneyConsumeMsgEumn.MSG_3,estimatePrice,companyId, order.getId()+""));
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
}
