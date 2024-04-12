package com.cmhq.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cmhq.core.dao.FaRechargeDao;
import com.cmhq.core.enums.MoneyConsumeEumn;
import com.cmhq.core.enums.MoneyConsumeMsgEumn;
import com.cmhq.core.model.CompanyMoneyParam;
import com.cmhq.core.model.FaCompanyEntity;
import com.cmhq.core.model.FaCompanyMoneyEntity;
import com.cmhq.core.model.FaRechargeEntity;
import com.cmhq.core.model.param.FaRechargeQuery;
import com.cmhq.core.service.FaCompanyMoneyService;
import com.cmhq.core.service.FaRechargeService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.QueryResult;
import me.zhengjie.annotation.Log;
import me.zhengjie.utils.SecurityUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Created by Jiyang.Zheng on 2024/4/9 19:47.
 */
@Slf4j
@Service
public class FaRechargeServiceImpl  implements FaRechargeService {
    @Autowired
    private FaCompanyMoneyService faCompanyMoneyService;

    @Autowired
    private FaRechargeDao faRechargeDao;

    @Override
    public QueryResult list(FaRechargeQuery query) {
        PageHelper.startPage(query.getPageNo(), query.getPageSize());
        LambdaQueryWrapper<FaRechargeEntity> queryWrapper = new LambdaQueryWrapper<>();
        if (SecurityUtils.getCurrentCompanyId() != null){
            queryWrapper.eq(FaRechargeEntity::getCid,SecurityUtils.getCurrentCompanyId());
        }
        if (StringUtils.isNotEmpty(query.getCompanyName())){
            queryWrapper.inSql(FaRechargeEntity::getCid,"select id from fa_company like '%"+query.getCompanyName()+"%'");
        }
        if (StringUtils.isNotEmpty(query.getOrderId())){
            queryWrapper.inSql(FaRechargeEntity::getOrderid,query.getOrderId());
        }
        if (StringUtils.isNotEmpty(query.getStatus())){
            queryWrapper.like(FaRechargeEntity::getStatus,query.getStatus());
        }
        if (StringUtils.isNotEmpty(query.getSTime())){
            queryWrapper.ge(FaRechargeEntity::getCreateTime, query.getSTime())
                    .le(FaRechargeEntity::getCreateTime,query.getETime());
        }
        if (StringUtils.isNotEmpty(query.getPlaySTime())){
            queryWrapper.ge(FaRechargeEntity::getUpdateTime, query.getPlaySTime())
                    .le(FaRechargeEntity::getUpdateTime,query.getETime());
        }
        queryWrapper.orderByDesc(FaRechargeEntity::getCreateTime);
        List<FaRechargeEntity> list = faRechargeDao.selectList(queryWrapper);
        PageInfo<FaRechargeEntity> page = new PageInfo<>(list);
        QueryResult<FaRechargeEntity> queryResult = new QueryResult<>();
        queryResult.setItems(list);
        queryResult.setTotal(page.getTotal());
        return queryResult;
    }

    @Override
    public Integer edit(FaCompanyEntity entity) {
        return null;
    }

    @Override
    public void delete(Integer id) {

    }

    @Transactional
    @Override
    public void applySuccessHandle(String outTradeNo, String tradeNo) {
        FaRechargeEntity faRecharge = faRechargeDao.selectOne(new LambdaQueryWrapper<FaRechargeEntity>().eq(FaRechargeEntity::getOrderid,outTradeNo));
        if (faRecharge == null){
            log.error("接支付返回状态，未查询到订单信息，更新失败。{},{}",outTradeNo,tradeNo);
            return;
        }
        FaRechargeEntity ue = new FaRechargeEntity();
        ue.setApplyTradeNo(tradeNo);
        ue.setStatus(1);
        ue.setId(faRecharge.getId());
        ue.setUpdateTime(new Date());
        faRechargeDao.updateById(ue);
        CompanyMoneyParam param = new CompanyMoneyParam(1,MoneyConsumeEumn.CONSUM_2, MoneyConsumeMsgEumn.MSG_1, faRecharge.getMoney(),faRecharge.getCid(),outTradeNo);
        //插入消费记录
        faCompanyMoneyService.saveRecord(param );
    }
}