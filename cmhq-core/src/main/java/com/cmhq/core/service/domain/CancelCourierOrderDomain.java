package com.cmhq.core.service.domain;

import com.cmhq.core.api.UploadResult;
import com.cmhq.core.api.UploadTypeEnum;
import com.cmhq.core.api.strategy.StrategyFactory;
import com.cmhq.core.api.strategy.Upload;
import com.cmhq.core.dao.FaCompanyDao;
import com.cmhq.core.dao.FaCourierOrderDao;
import com.cmhq.core.model.FaCompanyDayOpeNumEntity;
import com.cmhq.core.model.FaCompanyEntity;
import com.cmhq.core.model.FaCourierOrderEntity;
import com.cmhq.core.model.dto.CreateCourierOrderResponseDto;
import com.cmhq.core.service.FaCompanyDayOpeNumService;
import com.cmhq.core.service.FaCompanyService;
import com.cmhq.core.service.FaCourierOrderService;
import com.cmhq.core.util.CurrentUserContent;
import com.cmhq.core.util.SpringApplicationUtils;

import java.time.LocalDate;
import java.util.Date;
import java.util.Objects;

/**
 * Created by Jiyang.Zheng on 2024/4/11 10:31.
 */
public class CancelCourierOrderDomain {
    //内容
    private String content;
    //订单id
    Integer orderId;

    private FaCourierOrderDao faCourierOrderDao;
    private FaCourierOrderService faCourierOrderService;
    private  FaCompanyDayOpeNumService faCompanyDayOpeNumService;
    private final FaCompanyDao faCompanyDao;

    public CancelCourierOrderDomain(String content, Integer orderId) {
        this.content = content;
        this.orderId = orderId;
        faCourierOrderDao = SpringApplicationUtils.getBean(FaCourierOrderDao.class);
        faCompanyDayOpeNumService = SpringApplicationUtils.getBean(FaCompanyDayOpeNumService.class);
        faCompanyDao = SpringApplicationUtils.getBean(FaCompanyDao.class);
    }

    public void handle(){
        FaCourierOrderEntity entity = faCourierOrderDao.selectById(orderId);
        if (entity == null){
            throw new RuntimeException("订单不存在");
        }
        //待取件
        if ((Objects.equals(entity.getOrderState(),1) || !Objects.equals(entity.getOrderState(),0))
                //正常状态
                && (entity.getCancelOrderState() == 0 || entity.getCancelOrderState() == 1 || entity.getCancelOrderState() == 3)
                //没有物流信息
                && entity.getWuliuState() == null){
        }else {
            throw new RuntimeException("当前订单状态不允许取消！");
        }
        doCancel(entity);

    }

    private void check(FaCourierOrderEntity entity){
        FaCompanyEntity companyEntity = faCompanyDao.selectById(entity.getFaCompanyId());
        if (companyEntity.getCancelMaxMoney() != null){
            //商户日最大金额
            double createOrderMaxMoney = faCompanyDayOpeNumService.selectValue(LocalDate.now().toString(),LocalDate.now().toString(), FaCompanyDayOpeNumEntity.TYPE_ORDER_CANCEL_MAX_MONEY);
            if (createOrderMaxMoney >= companyEntity.getCancelMaxMoney()){
                throw new RuntimeException("商户当日取消最大金额已达上限");
            }
        }

        if (companyEntity.getCancelMaxNum() != null){
            //商户日取消次数
            double createOrderMaxMoney = faCompanyDayOpeNumService.selectValue(LocalDate.now().toString(),LocalDate.now().toString(), FaCompanyDayOpeNumEntity.TYPE_ORDER_CANCEL_NUM);
            if (createOrderMaxMoney >= companyEntity.getCancelMaxNum()){
                throw new RuntimeException("商户当日取消次数已达上限");
            }
        }

        if (CurrentUserContent.isCompanyChildUser() && CurrentUserContent.getCrrentUser().getChildUser() != null){
            if (CurrentUserContent.getCrrentUser().getChildUser().getCancelMaxMoney() != null){
                //账户日最大金额
                double createOrderMaxMoney = faCompanyDayOpeNumService.selectValue(LocalDate.now().toString(),LocalDate.now().toString(), FaCompanyDayOpeNumEntity.TYPE_ORDER_CANCEL_MAX_MONEY);
                if (createOrderMaxMoney >= CurrentUserContent.getCrrentUser().getChildUser().getCancelMaxMoney()){
                    throw new RuntimeException("账户当日取消最大金额已达上限");
                }
            }
            if (CurrentUserContent.getCrrentUser().getChildUser().getCancelMaxNum() != null){
                //账户日取消次数
                double createOrderMaxMoney = faCompanyDayOpeNumService.selectValue(LocalDate.now().toString(),LocalDate.now().toString(), FaCompanyDayOpeNumEntity.TYPE_ORDER_CANCEL_NUM);
                if (createOrderMaxMoney >= CurrentUserContent.getCrrentUser().getChildUser().getCancelMaxNum()){
                    throw new RuntimeException("账户当日取消次数已达上限");
                }
            }
        }

    }

    private void doCancel(FaCourierOrderEntity entity){
        try {
            //触发限制则审核
            check(entity);
        }catch (RuntimeException e){
            faCompanyDayOpeNumService.updateValue(LocalDate.now().toString(), FaCompanyDayOpeNumEntity.TYPE_ORDER_CANCEL_NUM,1);
            faCompanyDayOpeNumService.updateValue(LocalDate.now().toString(), FaCompanyDayOpeNumEntity.TYPE_ORDER_CANCEL_MAX_MONEY,entity.getPrice());
            FaCourierOrderEntity updateState = new FaCourierOrderEntity();
            updateState.setId(orderId);
            updateState.setCancelOrderState(-1);
            updateState.setCancelTime(new Date());
            updateState.setReason(content);
            updateState.setCancelType(1);
            entity.setReason(content);
            faCourierOrderDao.updateById(updateState);
            return;
        }
        FaCourierOrderEntity updateState = new FaCourierOrderEntity();
        updateState.setId(orderId);
        updateState.setCancelOrderState(-1);
        updateState.setCancelTime(new Date());
        updateState.setReason(content);
        updateState.setCancelType(1);
        entity.setReason(content);
        faCourierOrderDao.updateById(updateState);
        faCompanyDayOpeNumService.updateValue(LocalDate.now().toString(), FaCompanyDayOpeNumEntity.TYPE_ORDER_CANCEL_NUM,1);
        faCompanyDayOpeNumService.updateValue(LocalDate.now().toString(), FaCompanyDayOpeNumEntity.TYPE_ORDER_CANCEL_MAX_MONEY,entity.getPrice());
        //未触发限制直接审核通过取消
        new AuditSuccessCourierOrderDomain("", entity.getId()).handle();
//        FaCourierOrderEntity updateState = new FaCourierOrderEntity();
//        updateState.setId(orderId);
//        updateState.setCancelOrderState(-1);
//        updateState.setCancelTime(new Date());
//        updateState.setReason(content);
//        updateState.setCancelType(1);
//        entity.setReason(content);
//        faCourierOrderDao.updateById(updateState);
    }
}
