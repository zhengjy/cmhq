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
        check(entity);
        FaCourierOrderEntity updateState = new FaCourierOrderEntity();
        updateState.setId(orderId);
        updateState.setCancelOrderState(-1);
        updateState.setCancelTime(new Date());
        updateState.setReason(content);
        updateState.setCancelType(1);
        entity.setReason(content);
        faCourierOrderDao.updateById(updateState);
        //取消
        Upload upload = StrategyFactory.getUpload(Objects.requireNonNull(UploadTypeEnum.getMsgByCode(entity.getCourierCompanyCode(), UploadTypeEnum.TYPE_STO_CANCEL_COURIER_ORDER.getCodeNickName())));
        UploadResult uploadResult = upload.execute(entity);
        if (uploadResult.getFlag()){
        }else {
            throw new RuntimeException("物流公司取消失败:"+uploadResult.getErrorMsg());
        }

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


        //待取件
        if (Objects.equals(entity.getOrderState(),0)
                //正常状态
                && (entity.getCancelOrderState() == 0 || entity.getCancelOrderState() == 1 || entity.getCancelOrderState() == 3)
                //没有物流信息
                && entity.getWuliuState() == null){
        }else {
            throw new RuntimeException("当前订单状态不允许取消！");
        }
    }
}
