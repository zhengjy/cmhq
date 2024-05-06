package com.cmhq.core.quartz;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cmhq.core.api.UploadResult;
import com.cmhq.core.api.UploadTypeEnum;
import com.cmhq.core.api.strategy.StrategyFactory;
import com.cmhq.core.api.strategy.Upload;
import com.cmhq.core.dao.FaCourierOrderDao;
import com.cmhq.core.dao.FaCourierOrderExtDao;
import com.cmhq.core.enums.CourierOrderStateEnum;
import com.cmhq.core.enums.MoneyConsumeEumn;
import com.cmhq.core.enums.MoneyConsumeMsgEumn;
import com.cmhq.core.model.*;
import com.cmhq.core.model.dto.CreateCourierOrderResponseDto;
import com.cmhq.core.service.FaCompanyMoneyService;
import com.cmhq.core.service.FaCompanyService;
import com.cmhq.core.service.FaCourierOrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service("courierOrderTask")
public class CourierOrderTask {
    @Autowired
    private FaCourierOrderDao faCourierOrderDao;
    @Autowired
    private FaCompanyMoneyService faCompanyMoneyService;
    @Autowired
    private FaCompanyService faCompanyService;
    @Autowired
    FaCourierOrderService faCourierOrderService;
    @Autowired
    FaCourierOrderExtDao faCourierOrderExtDao;


    public void checkCourierTimeout24(){
        log.info("=====执行定时下发24小时超市未取件订单切换物流公司=====");
        LambdaQueryWrapper<FaCourierOrderEntity> lam = new LambdaQueryWrapper<>();
        String sd = DateUtil.format(LocalDateTime.now().minusHours(24), "yyyy-MM-dd mm:hh:ss");
        String ed = DateUtil.format(LocalDateTime.now(), "yyyy-MM-dd mm:hh:ss");
        lam.ge(FaCourierOrderEntity::getCreateTime, sd)
                .le(FaCourierOrderEntity::getCreateTime,ed);
        lam.eq(FaCourierOrderEntity::getOrderState, CourierOrderStateEnum.STATE_0.getType());
        List<FaCourierOrderEntity> list = faCourierOrderDao.selectList(lam);
        if (CollectionUtils.isEmpty(list)){
            log.info("=====执行定时下发24小时超市未取件订单切换物流公司结束=====");
            return;
        }

        try {
            for (FaCourierOrderEntity order : list){
                handle(order);
            }
        }catch (Exception e){
            log.error("",e);
        }

        log.info("=====执行定时下发24小时超市未取件订单切换物流公司结束=====");

    }

    @Transactional
    public void handle(FaCourierOrderEntity order){
        //先取消物流公司订单
        cancel(order);
        //删除订单
        faCourierOrderDao.deleteById(order.getId());
        //重新下单
        saveOrder(order);
    }

    private void saveOrder(FaCourierOrderEntity order){
        double price = 0;
        try {
            Integer oldOrderId = order.getId();
            Integer companyId = order.getFaCompanyId();
            //  实际费用
            price = order.getPrice();
            order.setId(null);
            //下单
            faCourierOrderDao.insert(order);
            //额外字段更新
            FaCourierOrderExtEntity extEntity = new FaCourierOrderExtEntity();
            extEntity.setCourierOrderId(order.getId());
            faCourierOrderExtDao.update(extEntity,new LambdaQueryWrapper<FaCourierOrderExtEntity>().eq(FaCourierOrderExtEntity::getCourierOrderId,order.getId()));
            //上传订单信息到物流公司
            String billNo = uploadCourierOrder(order);
            //插入消费记录
            faCompanyMoneyService.saveRecord(new CompanyMoneyParam(2, MoneyConsumeEumn.CONSUM_3, MoneyConsumeMsgEumn.MSG_3,price,companyId, order.getId()+"",billNo));

        }catch (Exception e){
            log.error("",e);
            throw new RuntimeException(e.getMessage());
        }
    }

    private void  cancel(FaCourierOrderEntity order){
        double price = order.getPrice();
        //插入返还记录 &返还商户或扣除商户金额
        faCompanyMoneyService.saveRecord(new CompanyMoneyParam(1, MoneyConsumeEumn.CONSUM_1, MoneyConsumeMsgEumn.MSG_8,price,order.getFaCompanyId(),order.getId()+"",order.getCourierCompanyWaybillNo()));
        //调用接口取消
        Upload upload = StrategyFactory.getUpload(Objects.requireNonNull(UploadTypeEnum.getMsgByCode(order.getCourierCompanyCode(), UploadTypeEnum.TYPE_STO_CANCEL_COURIER_ORDER.getCodeNickName())));
        UploadResult uploadResult = upload.execute(order);
        if (!uploadResult.getFlag()) {
            throw new RuntimeException("物流公司取消失败:"+uploadResult.getErrorMsg());
        }
    }


    private String uploadCourierOrder(FaCourierOrderEntity order){
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
                updateEntity.setOrderNo(rsp.getOrderNo());
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
}
