package com.cmhq.core.service.domain;

import com.alibaba.fastjson.JSONObject;
import com.cmhq.core.api.UploadResult;
import com.cmhq.core.api.UploadTypeEnum;
import com.cmhq.core.api.dto.request.CourierOrderSubscribeTraceDto;
import com.cmhq.core.api.strategy.StrategyFactory;
import com.cmhq.core.api.strategy.Upload;
import com.cmhq.core.dao.FaCourierOrderDao;
import com.cmhq.core.enums.MoneyConsumeEumn;
import com.cmhq.core.enums.MoneyConsumeMsgEumn;
import com.cmhq.core.model.param.CompanyMoneyParam;
import com.cmhq.core.model.FaCompanyDayOpeNumEntity;
import com.cmhq.core.model.FaCourierOrderEntity;
import com.cmhq.core.model.param.CourierOrderQuery;
import com.cmhq.core.service.FaCompanyDayOpeNumService;
import com.cmhq.core.service.FaCompanyMoneyService;
import com.cmhq.core.service.FaCourierOrderService;
import com.cmhq.core.util.SpringApplicationUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * 退单号创建
 * Created by Jiyang.Zheng on 2024/4/10 15:18.
 */
@Slf4j
public class ReturnOrderCreateCourierOrderDomain {

    private final FaCourierOrderEntity order;
    private final FaCourierOrderDao faCourierOrderDao;
    private final FaCourierOrderService faCourierOrderService;
    private final FaCompanyMoneyService faCompanyMoneyService;
    private final FaCompanyDayOpeNumService faCompanyDayOpeNumService;

    public ReturnOrderCreateCourierOrderDomain(FaCourierOrderEntity param){
        faCourierOrderDao = SpringApplicationUtils.getBean(FaCourierOrderDao.class);
        faCourierOrderService = SpringApplicationUtils.getBean(FaCourierOrderService.class);
        faCompanyMoneyService = SpringApplicationUtils.getBean(FaCompanyMoneyService.class);
        faCompanyDayOpeNumService = SpringApplicationUtils.getBean(FaCompanyDayOpeNumService.class);

        order = new FaCourierOrderEntity();
        order.setOrderNo(System.currentTimeMillis()+"");
        order.setCourierCompanyCode(param.getCourierCompanyCode());
        order.setCourierCompanyOrderNo("");
        order.setCourierCompanyWaybillNo(param.getCourierCompanyWaybillNo());
        order.setFaCompanyId(param.getFaCompanyId());
        order.setZid(param.getZid());
        order.setGoodsName(param.getGoodsName());
        order.setWeight(param.getWeight());
        order.setWeightto(param.getWeightto());
        order.setWidth(param.getWidth());
        order.setHeight(param.getHeight());
        order.setLength(param.getLength());
        order.setEstimatePrice(param.getEstimatePrice());
        order.setPrice(param.getPrice());
        order.setType(1);
        order.setOrderState(0);
        order.setCancelOrderState(1);

        order.setFromName(param.getToName());
        order.setFromMobile(param.getToMobile());
        order.setFromProv(param.getToProv());
        order.setFromCity(param.getToCity());
        order.setFromArea(param.getToArea());
        order.setFromAddress(param.getToAddress());

        order.setToName(param.getFromName());
        order.setToMobile(param.getFromMobile());
        order.setToProv(param.getFromProv());
        order.setToCity(param.getFromCity());
        order.setToArea(param.getFromArea());
        order.setToAddress(param.getFromAddress());


        order.setOrderIsError(1);
        order.setIsJiesuan(0);
        order.setMsg(param.getMsg());
        order.setCreateUserId(param.getCreateUserId());
        order.setTakeGoodsTimeEnd(param.getTakeGoodsTimeEnd());
        order.setCourierOrderExtend(param.getCourierOrderExtend());
        CourierOrderQuery query = new CourierOrderQuery();
        query.setId(param.getId());
        List<FaCourierOrderEntity> list = faCourierOrderService.queryAll(query).getItems();
        if (CollectionUtils.isNotEmpty(list)){
            order.setCourierOrderExtend(list.get(0).getCourierOrderExtend());
        }

    }



    public Integer handle(){
        double price = order.getPrice();
        Integer companyId = order.getFaCompanyId();
        try {
            //下单
            faCourierOrderDao.insert(order);
            //额外字段插入
            saveOrderExt(order.getId(), order.getCourierOrderExtend());
            CourierOrderSubscribeTraceDto dto = new CourierOrderSubscribeTraceDto();
            dto.setWaybillCode(order.getCourierCompanyWaybillNo());
            dto.setMobile(order.getToMobile());
            //上传物流公司
            Upload upload = StrategyFactory.getUpload(Objects.requireNonNull(UploadTypeEnum.getMsgByCode(order.getCourierCompanyCode(), UploadTypeEnum.TYPE_JD_ORDER_SUBSCRIBE_TRACE.getCodeNickName())));
            UploadResult uploadResult = upload.execute(dto);
            if (uploadResult.getFlag()){
            }else {
                throw new RuntimeException("订阅状态失败:"+uploadResult.getErrorMsg());
            }
            //插入消费记录
            faCompanyMoneyService.saveRecord(new CompanyMoneyParam(2, MoneyConsumeEumn.CONSUM_3, MoneyConsumeMsgEumn.MSG_7,price,companyId, order.getId()+"",order.getCourierCompanyWaybillNo()));
            //记录统计量
            faCompanyDayOpeNumService.updateValue(LocalDate.now().toString(),FaCompanyDayOpeNumEntity.TYPE_ORDER_CREATE_NUM,1,companyId);
            faCompanyDayOpeNumService.updateValue(LocalDate.now().toString(),FaCompanyDayOpeNumEntity.TYPE_ORDER_CREATE_MAX_MONEY,price,companyId);
        }catch (Exception e){
            log.error("",e);
            throw new RuntimeException(e.getMessage());
        }
        return order.getId();
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
