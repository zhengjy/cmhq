package com.cmhq.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cmhq.core.api.UploadResult;
import com.cmhq.core.api.UploadTypeEnum;
import com.cmhq.core.api.strategy.StrategyFactory;
import com.cmhq.core.api.strategy.Upload;
import com.cmhq.core.dao.FaCourierOrderExtDao;
import com.cmhq.core.dao.FaCourierOrderDao;
import com.cmhq.core.model.FaCourierOrderEntity;
import com.cmhq.core.model.FaCourierOrderExtEntity;
import com.cmhq.core.model.param.CourierOrderQuery;
import com.cmhq.core.service.FaCourierOrderService;
import com.cmhq.core.service.domain.CourierOrderDomain;
import com.cmhq.core.service.domain.CourierOrderQueryPageDomain;
import me.zhengjie.QueryResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
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
        CourierOrderDomain domain = new CourierOrderDomain(entity);
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
        return "";
    }

    @Transactional
    @Override
    public String cancelCourierOrder(String stateType,String content,Integer id) {
        FaCourierOrderEntity updateState = new FaCourierOrderEntity();
        updateState.setId(id);

        if (StringUtils.equals(stateType,"cancel")){
            updateState.setCancelOrderState(-1);
            saveOrderExt(id,FaCourierOrderExtEntity.CNAME_CANCLE_CONTENT,content);
            saveOrderExt(id,FaCourierOrderExtEntity.CNAME_CANCLE_TIME, LocalDateTime.now().toString());
            FaCourierOrderEntity entity = faCourierOrderDao.selectById(id);
            Upload upload = StrategyFactory.getUpload(Objects.requireNonNull(UploadTypeEnum.getMsgByCode(entity.getCourierCompanyCode(), UploadTypeEnum.TYPE_STO_CANCEL_COURIER_ORDER.getCodeNickName())));
            UploadResult uploadResult = upload.execute(entity);
            if (!uploadResult.getFlag()) {
                throw new RuntimeException("物流公司取消失败:"+uploadResult.getErrorMsg());
            }
        }else if (StringUtils.equals(stateType,"auditFail")){
            updateState.setCancelOrderState(3);
        }else if (StringUtils.equals(stateType,"auditSuccess")){
            //TODO 审核通过后处理
            updateState.setCancelOrderState(2);
        }
        faCourierOrderDao.updateById(updateState);


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
