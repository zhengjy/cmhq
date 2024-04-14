package com.cmhq.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cmhq.core.api.UploadResult;
import com.cmhq.core.api.UploadTypeEnum;
import com.cmhq.core.api.dto.response.CourierFreightChargeDto;
import com.cmhq.core.api.strategy.StrategyFactory;
import com.cmhq.core.api.strategy.Upload;
import com.cmhq.core.dao.FaCourierOrderExtDao;
import com.cmhq.core.dao.FaCourierOrderDao;
import com.cmhq.core.model.FaCourierOrderEntity;
import com.cmhq.core.model.FaCourierOrderExtEntity;
import com.cmhq.core.model.param.CourierOrderQuery;
import com.cmhq.core.service.FaCourierOrderService;
import com.cmhq.core.service.domain.AuditSuccessCourierOrderDomain;
import com.cmhq.core.service.domain.CancelCourierOrderDomain;
import com.cmhq.core.service.domain.CreateCourierOrderDomain;
import com.cmhq.core.service.domain.CourierOrderQueryPageDomain;
import me.zhengjie.QueryResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public Object getCourierFreightCharge(FaCourierOrderEntity entity) {
        entity.setOrderNo(System.currentTimeMillis()+"");
        Upload upload = StrategyFactory.getUpload(Objects.requireNonNull(UploadTypeEnum.getMsgByCode(entity.getCourierCompanyCode(), UploadTypeEnum.TYPE_STO_QUERY_FREIGHT_CHARGE.getCodeNickName())));
        UploadResult uploadResult = upload.execute(entity);
        if (uploadResult.getFlag()){
            return uploadResult.getJsonMsg();
        }
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
        Upload upload = StrategyFactory.getUpload(Objects.requireNonNull(UploadTypeEnum.getMsgByCode(entity.getCourierCompanyCode(), UploadTypeEnum.TYPE_STO_COURIER_QUERY_TRACK.getCodeNickName())));
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
