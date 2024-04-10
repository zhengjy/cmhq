package com.cmhq.core.service.domain;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cmhq.core.api.UploadResult;
import com.cmhq.core.api.UploadTypeEnum;
import com.cmhq.core.api.strategy.StrategyFactory;
import com.cmhq.core.api.strategy.Upload;
import com.cmhq.core.dao.FaCompanyDao;
import com.cmhq.core.dao.FaCourierOrderExtDao;
import com.cmhq.core.dao.FaCourierOrderDao;
import com.cmhq.core.model.FaCompanyEntity;
import com.cmhq.core.model.FaCourierOrderEntity;
import com.cmhq.core.model.FaCourierOrderExtEntity;
import com.cmhq.core.service.FaCourierOrderService;
import com.cmhq.core.util.CurrentUserContent;
import com.cmhq.core.util.SpringApplicationUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * Created by Jiyang.Zheng on 2024/4/10 15:18.
 */
@Slf4j
public class CourierOrderDomain {

    private FaCourierOrderEntity entity;
    private final FaCourierOrderDao faCourierOrderDao;
    private final FaCourierOrderExtDao faCourierOrderExtDao;
    private final FaCourierOrderService faCourierOrderService;
    private final FaCompanyDao faCompanyDao;

    public CourierOrderDomain(FaCourierOrderEntity entity) {
        this.entity = entity;
        faCourierOrderDao = SpringApplicationUtils.getBean(FaCourierOrderDao.class);
        faCourierOrderExtDao = SpringApplicationUtils.getBean(FaCourierOrderExtDao.class);
        faCourierOrderService = SpringApplicationUtils.getBean(FaCourierOrderService.class);
        faCompanyDao = SpringApplicationUtils.getBean(FaCompanyDao.class);
    }

    public Integer handle(){
        try {
            FaCompanyEntity faCompanyEntity = faCompanyDao.selectById(entity.getFaCompanyId());
            //账户余额不足 订单扣费金额  TODO 预扣款字段
            double money = entity.getMoney() * ((double) faCompanyEntity.getRatio() /100);
            if (faCompanyEntity.getMoney()  < money){
                throw new RuntimeException("账户余额不足");
            }
            //扣除余额，更新预扣款
            int num = faCompanyDao.updateMoney(entity.getFaCompanyId(),money);
            if (num < 1){
                throw new RuntimeException("更新账户余额失败");
            }
            if(CurrentUserContent.isCompanyUser()){
            //子账户校验笔数、单笔、多笔最大金额
            }else if (CurrentUserContent.isCompanyChildUser()){
                if (CurrentUserContent.getCrrentUser().getChildUser() != null){
                    if ( entity.getMoney() > CurrentUserContent.getCrrentUser().getChildUser().getZiOneMaxMoney()){
                        throw new RuntimeException("子账户单笔金额达到上线");//TODO 其他参数校验
                    }
                }
            }
            entity.setCancelOrderState(1);
            entity.setOrderNo(System.currentTimeMillis() + "");
            entity.setIsJiesuan(0);
            faCourierOrderDao.insert(entity);
            //额外字段插入
            saveOrderExt(entity.getId(),entity.getCourierOrderExtend());

            //上传物流公司
            Upload upload = StrategyFactory.getUpload(Objects.requireNonNull(UploadTypeEnum.getMsgByCode(entity.getCourierCompanyCode(), UploadTypeEnum.TYPE_STO_ORDER_UPLOAD.getCodeNickName())));
            UploadResult uploadResult = upload.execute(entity);
            if (uploadResult.getFlag()){
                JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(uploadResult.getJsonMsg()));
                FaCourierOrderEntity updateEntity = new FaCourierOrderEntity();
                updateEntity.setId(entity.getId());
                if (StringUtils.isEmpty(jsonObject.getString("waybillNo"))){
                    throw new RuntimeException("物流公司未返回运单号");
                }
                updateEntity.setCourierCompanyWaybillNo(jsonObject.getString("waybillNo"));
                updateEntity.setCourierOrderNo(jsonObject.getString("orderNo"));
                faCourierOrderDao.updateById(updateEntity);
            }else {
                throw new RuntimeException("物流公司上传失败:"+uploadResult.getErrorMsg());
            }


        }catch (Exception e){
            log.error("",e);
            throw new RuntimeException(e.getMessage());
        }finally {
        }
        return entity.getId();
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
