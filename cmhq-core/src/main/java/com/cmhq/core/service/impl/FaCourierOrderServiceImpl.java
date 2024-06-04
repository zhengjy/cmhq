package com.cmhq.core.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cmhq.core.api.UploadResult;
import com.cmhq.core.api.UploadTypeEnum;
import com.cmhq.core.api.strategy.StrategyFactory;
import com.cmhq.core.api.strategy.Upload;
import com.cmhq.core.dao.*;
import com.cmhq.core.enums.CourierCompanyEnum;
import com.cmhq.core.fitler.FreightFilter;
import com.cmhq.core.model.*;
import com.cmhq.core.model.dto.FaCostFreight;
import com.cmhq.core.model.dto.FreightChargeDto;
import com.cmhq.core.model.param.CourierOrderQuery;
import com.cmhq.core.service.FaCourierOrderService;
import com.cmhq.core.service.domain.AuditSuccessCourierOrderDomain;
import com.cmhq.core.service.domain.CancelCourierOrderDomain;
import com.cmhq.core.service.domain.CreateCourierOrderDomain;
import com.cmhq.core.service.domain.CourierOrderQueryPageDomain;
import com.cmhq.core.util.EstimatePriceUtil;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.QueryResult;
import me.zhengjie.modules.system.service.UserService;
import me.zhengjie.modules.system.service.dto.UserDto;
import me.zhengjie.utils.SecurityUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Jiyang.Zheng on 2024/4/6 14:00.
 */
@Slf4j
@Service
public class FaCourierOrderServiceImpl implements FaCourierOrderService {

    @Autowired
    private FaCourierOrderDao faCourierOrderDao;
    @Autowired
    private FaCourierOrderExtDao faCourierOrderExtDao;
    @Resource(name = "jTFreightFilter")
    private FreightFilter freightFilter;
    @Autowired
    private FaCourierOrderShareOrdernoDao faCourierOrderShareOrdernoDao;
    @Autowired
    private FaCompanyDao faCompanyDao;
    @Autowired
    private UserService userService;

    @Override
    public QueryResult<FaCourierOrderEntity> queryAll(CourierOrderQuery query) {
        return new CourierOrderQueryPageDomain(query).handle();
    }

    @Transactional
    @Override
    public Integer create(FaCourierOrderEntity entity) {
        CreateCourierOrderDomain domain = new CreateCourierOrderDomain(entity);
        return domain.handle();

    }

    @Override
    public Object batchCreate(List<FaCourierOrderEntity> resources) {
            resources.forEach(v ->{
                try {
			v.setId(null);
                    CreateCourierOrderDomain domain = new CreateCourierOrderDomain(v);
                    domain.handle();
                }catch (Exception e){
                    log.error("创建失败: param【{}】",JSONObject.toJSONString(v));
                    log.error("",e);
                }
            });
            return null;


    }

    @Transactional
    @Override
    public Integer shareCreate(FaCourierOrderEntity resources) {
        try {
            log.info("分享创建 header orderNo={},companyId={},userId={}",resources.getOrderNoHeader(),resources.getCompanyIdHeader(),resources.getUserIdHeader());
            String orderNoStr = new String(Base64.getDecoder().decode(resources.getOrderNoHeader()));
            FaCourierOrderShareOrdernoEntity orderShareOrdernoEntity = faCourierOrderShareOrdernoDao.selectOne(new LambdaQueryWrapper<FaCourierOrderShareOrdernoEntity>().eq(FaCourierOrderShareOrdernoEntity::getOrderNo,orderNoStr));
            if (orderShareOrdernoEntity == null){
                throw new RuntimeException("不允许创建订单，订单号不存在");
            }
            String companyIdStr = new String(Base64.getDecoder().decode(resources.getCompanyIdHeader()));
            FaCompanyEntity faCompany = faCompanyDao.selectById(Integer.parseInt(companyIdStr));
            if (faCompany == null){
                throw new RuntimeException("不允许创建订单，商户不存在");
            }
            String userIdStr = new String(Base64.getDecoder().decode(resources.getUserIdHeader()));
            UserDto userDto = userService.findById(Long.parseLong(userIdStr));
            if (userDto == null){
                throw new RuntimeException("不允许创建订单，用户不存在");
            }
             CreateCourierOrderDomain domain = new CreateCourierOrderDomain(resources,Integer.parseInt(companyIdStr),Long.parseLong(userIdStr),orderNoStr);
            return domain.handle();
        }catch (Exception e){
            log.error("执行分享创建失败 {}", JSONObject.toJSONString(resources));
            log.error("执行分享创建失败",e);
            throw new RuntimeException("执行分享创建失败:"+e.getMessage());
        }
    }


    @Override
    public FreightChargeDto getCourierFreightCharge(FaCourierOrderEntity entity,Integer retio) {
//        FreightChargeDto freightChargeDto = freightFilter.getFreight(entity);
//        if (freightChargeDto.getTotalPrice() == null){
//            throw new RuntimeException("查询"+freightChargeDto.getCourierCompanyCode()+"运费返回错误:"+freightChargeDto.getErrorMsg());
//        }
        FreightChargeDto fcd = EstimatePriceUtil.getPrice(entity.getFromProv(),entity.getToProv(),entity.getFromCity(),entity.getToCity(),entity.getWeight(),retio);
        return fcd;
    }

    @Override
    public FaCostFreight selectFaCompanyCostFreight(FaCourierOrderEntity entity) {
        return null;
    }

    @Transactional
    @Override
    public String cancelCourierOrder(String stateType,String content,Integer id) {
        if (StringUtils.equals(stateType,"cancel")){
            new CancelCourierOrderDomain(content,id).handle();
        }else if (StringUtils.equals(stateType,"auditFail")){
            FaCourierOrderEntity updateState = new FaCourierOrderEntity();
            updateState.setId(id);
            updateState.setCancelOrderState(3);
            faCourierOrderDao.updateById(updateState);
        }else if (StringUtils.equals(stateType,"uncancel")){
            FaCourierOrderEntity updateState = new FaCourierOrderEntity();
            updateState.setId(id);
            updateState.setReason("-");
            updateState.setCancelOrderState(1);
            faCourierOrderDao.updateById(updateState);
        }else if (StringUtils.equals(stateType,"auditSuccess")){
            new AuditSuccessCourierOrderDomain(content,id).handle();
        }
        return "";
    }

    @Override
    public Object addressAnalysis(String text) {
        Upload upload = StrategyFactory.getUpload(UploadTypeEnum.TYPE_BAIDU_ADDRDESS);
        UploadResult uploadResult = upload.execute(text);
        if (uploadResult.getFlag()){
            return uploadResult.getJsonMsg();
        }
        return "";
    }

    @Override
    public Object queryCourierTrack(Integer id) {
        FaCourierOrderEntity entity = faCourierOrderDao.selectById(id);
        String courierCompanyCode = CourierCompanyEnum.COMPANY_STO.getType();
        if (entity == null){
            throw new RuntimeException("未查询到此订单");
        }
        if (StringUtils.isNotEmpty(entity.getCourierCompanyCode())){
            courierCompanyCode = entity.getCourierCompanyCode();
        }
        Upload upload = StrategyFactory.getUpload(Objects.requireNonNull(UploadTypeEnum.getMsgByCode(courierCompanyCode, UploadTypeEnum.TYPE_STO_COURIER_QUERY_TRACK.getCodeNickName())));
        UploadResult uploadResult = upload.execute(entity.getCourierCompanyWaybillNo());
        if (!uploadResult.getFlag()) {
            throw new RuntimeException("查询物流轨迹失败:"+uploadResult.getErrorMsg());
        }
        return uploadResult.getJsonMsg();
    }

    @Override
    public void saveOrderExt(Integer id,String cname,String cvalue){
        FaCourierOrderExtEntity extEntity =new FaCourierOrderExtEntity();
        extEntity.setCvalue(cvalue);
        int i = faCourierOrderExtDao.update(extEntity,new LambdaQueryWrapper<FaCourierOrderExtEntity>()
                .eq(FaCourierOrderExtEntity::getCourierOrderId,id).eq(FaCourierOrderExtEntity::getCname,cname));
        if (i <= 0){
            faCourierOrderExtDao.insert(new FaCourierOrderExtEntity(id,cname, cvalue));
        }
    }

    @Transactional
    @Override
    public void delete(Integer id) {
        FaCourierOrderEntity  order = faCourierOrderDao.selectById(id);
        if(order == null){
            return;
        }
        FaCourierOrderEntity entity = new FaCourierOrderEntity();
        entity.setId(id);
        entity.setXdelkid(1);
        faCourierOrderDao.updateById(entity);
    }

    @Override
    public void modifyOrder(FaCourierOrderEntity order) {
        FaCourierOrderEntity orderEntity = faCourierOrderDao.selectById(order.getId());
        order.setCourierCompanyWaybillNo(orderEntity.getCourierCompanyWaybillNo());
        Upload upload = StrategyFactory.getUpload(Objects.requireNonNull(UploadTypeEnum.getMsgByCode(orderEntity.getCourierCompanyCode(), UploadTypeEnum.TYPE_JD_ORDER_MODIFY.getCodeNickName())));
        UploadResult uploadResult = upload.execute(order);
        if (uploadResult.getFlag()){
            faCourierOrderDao.updateById(order);
        }else {
            throw new RuntimeException(uploadResult.getErrorMsg());
        }
    }

    @Override
    public Map<String, String> getOrderNoCompanyIdUserId() {
        FaCourierOrderShareOrdernoEntity entity = new FaCourierOrderShareOrdernoEntity();
        entity.setOrderNo(System.currentTimeMillis()+"");
        faCourierOrderShareOrdernoDao.insert(entity);
        Map<String,String> map = Maps.newHashMap();
        map.put("orderNo", Base64.getEncoder().encodeToString(entity.getOrderNo().getBytes()));
        map.put("companyId",Base64.getEncoder().encodeToString((SecurityUtils.getCurrentCompanyId()+"").getBytes() ));
        map.put("userId",Base64.getEncoder().encodeToString((SecurityUtils.getCurrentUserId()+"").getBytes()));
        return map;
    }
}
