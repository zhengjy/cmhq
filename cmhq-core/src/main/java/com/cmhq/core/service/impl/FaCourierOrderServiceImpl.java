package com.cmhq.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cmhq.core.api.UploadResult;
import com.cmhq.core.api.UploadTypeEnum;
import com.cmhq.core.api.strategy.StrategyFactory;
import com.cmhq.core.api.strategy.Upload;
import com.cmhq.core.dao.FaCourierOrderExtDao;
import com.cmhq.core.dao.FaCourierOrderDao;
import com.cmhq.core.enums.CourierCompanyEnum;
import com.cmhq.core.fitler.FreightFilter;
import com.cmhq.core.model.FaCourierOrderEntity;
import com.cmhq.core.model.FaCourierOrderExtEntity;
import com.cmhq.core.model.dto.FreightChargeDto;
import com.cmhq.core.model.param.CourierOrderQuery;
import com.cmhq.core.service.FaCourierOrderService;
import com.cmhq.core.service.domain.AuditSuccessCourierOrderDomain;
import com.cmhq.core.service.domain.CancelCourierOrderDomain;
import com.cmhq.core.service.domain.CreateCourierOrderDomain;
import com.cmhq.core.service.domain.CourierOrderQueryPageDomain;
import me.zhengjie.QueryResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.Objects;

/**
 * Created by Jiyang.Zheng on 2024/4/6 14:00.
 */
@Service
public class FaCourierOrderServiceImpl implements FaCourierOrderService {

    @Autowired
    private FaCourierOrderDao faCourierOrderDao;
    @Autowired
    private FaCourierOrderExtDao faCourierOrderExtDao;
    @Resource(name = "stoFreightFilter")
    private FreightFilter freightFilter;

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
    public FreightChargeDto getCourierFreightCharge(FaCourierOrderEntity entity) {
        entity.setOrderNo(System.currentTimeMillis()+"");
        return freightFilter.getFreight(entity);
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
        }else if (StringUtils.equals(stateType,"x")){
            FaCourierOrderEntity updateState = new FaCourierOrderEntity();
            updateState.setId(id);
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
        faCourierOrderExtDao.delete(new LambdaQueryWrapper<FaCourierOrderExtEntity>().eq(FaCourierOrderExtEntity::getCourierOrderId,id).eq(FaCourierOrderExtEntity::getCname,cname));
        faCourierOrderExtDao.insert(new FaCourierOrderExtEntity(id,cname, cvalue));
    }

}
