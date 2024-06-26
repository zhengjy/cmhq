package com.cmhq.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cmhq.core.dao.FaCompanyDao;
import com.cmhq.core.dao.FaCompanyMoneyDao;
import com.cmhq.core.dao.FaUserDao;
import com.cmhq.core.enums.MoneyConsumeEumn;
import com.cmhq.core.enums.MoneyConsumeMsgEumn;
import com.cmhq.core.model.param.CompanyMoneyParam;
import com.cmhq.core.model.FaCompanyEntity;
import com.cmhq.core.model.FaCompanyMoneyEntity;
import com.cmhq.core.model.FaUserEntity;
import com.cmhq.core.model.param.FcCompanyMoneyQuery;
import com.cmhq.core.service.FaCompanyMoneyService;
import com.cmhq.core.service.FaCompanyService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import me.zhengjie.QueryResult;
import me.zhengjie.utils.SecurityUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by Jiyang.Zheng on 2024/4/9 19:47.
 */
@Service
public class FaCompanyMoneyServiceImpl implements FaCompanyMoneyService {
    @Autowired
    private FaCompanyMoneyDao faCompanyMoneyDao;
    @Autowired
    private FaCompanyService faCompanyService;
    @Autowired
    private FaCompanyDao faCompanyDao;
    @Autowired
    private FaUserDao faUserDao;

    @Override
    public QueryResult list(FcCompanyMoneyQuery query) {
        PageHelper.startPage(query.getPageNo(), query.getPageSize());
        LambdaQueryWrapper<FaCompanyMoneyEntity> queryWrapper = new LambdaQueryWrapper<>();
        if (SecurityUtils.getCurrentCompanyId() != null){
            queryWrapper.eq(FaCompanyMoneyEntity::getCid,SecurityUtils.getCurrentCompanyId());
        }
        if (StringUtils.isNotEmpty(query.getCompanyName())){
            queryWrapper.inSql(FaCompanyMoneyEntity::getCid,"select id from fa_company where company_name like '%"+query.getCompanyName()+"%'");
        }
        if (StringUtils.isNotEmpty(query.getBillNo())){
            queryWrapper.like(FaCompanyMoneyEntity::getBillCode,query.getBillNo());
        }
        if (StringUtils.isNotEmpty(query.getType())){
            queryWrapper.eq(FaCompanyMoneyEntity::getType,query.getType());
        }
        queryWrapper.orderByDesc(FaCompanyMoneyEntity::getCreateTime);
        List<FaCompanyMoneyEntity> list = faCompanyMoneyDao.selectList(queryWrapper);
        PageInfo<FaCompanyMoneyEntity> page = new PageInfo<>(list);
        QueryResult<FaCompanyMoneyEntity> queryResult = new QueryResult<>();
        if (list != null){
            list.stream().forEach(v ->{
                FaCompanyEntity faCompanyEntity = faCompanyService.selectById(v.getCid());
                if (faCompanyEntity != null){
                    v.setCompanyName(faCompanyEntity.getCompanyName());
                    FaUserEntity faUserEntity = faUserDao.selectById(faCompanyEntity.getFUser());
                    if (faUserEntity != null){
                        v.setFaUsername(faUserEntity.getNickname());
                    }
                }

            });
        }
        queryResult.setItems(list);
        queryResult.setTotal(page.getTotal());
        return queryResult;
    }

    @Override
    public void delete(Integer id) {

    }

    @Transactional
    @Override
    public void saveRecord(CompanyMoneyParam param) {
        FaCompanyEntity faCompanyEntity = faCompanyService.selectById(param.getCompanyId());
        FaCompanyMoneyEntity e = new FaCompanyMoneyEntity();
        e.setAddType(param.getAddType());
        e.setCid(param.getCompanyId());
        e.setMoney(param.getMoney());
        e.setType(Integer.parseInt(param.getConsumeEumn().getType()));
        e.setMsg(param.getMsgEumn().getDesc());
        e.setBefore(faCompanyEntity.getMoney());
        e.setDelkid(0);
        e.setXdelkid(0);
        e.setOrderNo(param.getOrderId());
        e.setBillCode(param.getBillCode());
        //充值
        if (param.getConsumeEumn().getType().equals(MoneyConsumeEumn.CONSUM_2.getType())){
            e.setAfter(faCompanyEntity.getMoney()+param.getMoney());
            e.setKoufeiType(1);
            //更新商户金额
            faCompanyDao.addMoney(param.getCompanyId(),param.getMoney());
        }else if (param.getMsgEumn().getDesc().equals(MoneyConsumeMsgEumn.MSG_3.getDesc()) || param.getMsgEumn().getDesc().equals(MoneyConsumeMsgEumn.MSG_7.getDesc())){
            e.setAfter(faCompanyEntity.getMoney()-param.getMoney());
            //更新商户金额
            //扣除余额，更新预扣款
            int num = faCompanyDao.minusMoneyAndEstimatePrice(param.getCompanyId(),param.getMoney());
            if (num < 1){
                throw new RuntimeException("更新账户余额失败");
            }
        //取消订单退回预估费用
        }else if (param.getMsgEumn().getDesc().equals(MoneyConsumeMsgEumn.MSG_6.getDesc())
                || param.getMsgEumn().getDesc().equals(MoneyConsumeMsgEumn.MSG_5.getDesc())
                || param.getMsgEumn().getDesc().equals(MoneyConsumeMsgEumn.MSG_8.getDesc())
        ){
            e.setAfter(faCompanyEntity.getMoney()+param.getMoney());
            int num = faCompanyDao.addMoney(param.getCompanyId(),param.getMoney());
            if (num < 1){
                throw new RuntimeException("取消订单退回预估费用失败");
            }
        //货物签收退回预估费用
        }else if (param.getMsgEumn().getDesc().equals(MoneyConsumeMsgEumn.MSG_4.getDesc())){
            e.setAfter(faCompanyEntity.getMoney()+param.getMoney());
            int num = faCompanyDao.addMoneyAndEstimatePrice(param.getCompanyId(),param.getMoney());
            if (num < 1){
                throw new RuntimeException("货物签收退回预估费用");
            }
            //货物签收退扣除费用
        }else if (param.getMsgEumn().getDesc().equals(MoneyConsumeMsgEumn.MSG_2.getDesc()) ||
                param.getMsgEumn().getDesc().equals(MoneyConsumeMsgEumn.MSG_10.getDesc()) ||
                param.getMsgEumn().getDesc().equals(MoneyConsumeMsgEumn.MSG_11.getDesc()) ||
                param.getMsgEumn().getDesc().equals(MoneyConsumeMsgEumn.MSG_12.getDesc())
        ){
            e.setAfter(faCompanyEntity.getMoney()-param.getMoney());
            int num = faCompanyDao.minusMoney(param.getCompanyId(),param.getMoney());
            if (num < 1){
                throw new RuntimeException("货物签收扣除费用");
            }
        }
        faCompanyMoneyDao.insert(e);

    }


}
