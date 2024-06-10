package com.cmhq.core.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.cmhq.core.dao.FaUserDao;
import com.cmhq.core.dao.FaUserMoneyDao;
import com.cmhq.core.enums.UserMoneyConsumeMsgEumn;
import com.cmhq.core.model.FaUserEntity;
import com.cmhq.core.model.FaUserMoneyEntity;
import com.cmhq.core.model.param.UserMoneyParam;
import com.cmhq.core.service.FaUserMoneyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
@Service
public class FaUserMoneyServiceImpl implements FaUserMoneyService {
    @Autowired
    private FaUserDao faUserDao;
    @Autowired
    private FaUserMoneyDao faUserMoneyDao;

    @Transactional
    @Override
    public void saveRecord(UserMoneyParam param) {
        int retio = param.getDistributionRatio() == null  ? 0 : param.getDistributionRatio();
        FaUserEntity faUser = faUserDao.selectById(param.getUserId());
        if (faUser == null){
            log.error("插入用户消费记录，未查询到用户,param={}", JSONObject.toJSONString(param));
            return;
        }
        if (param.getMsgEumn().getDesc().equals(UserMoneyConsumeMsgEumn.MSG_1.getDesc())){
            Double money = BigDecimal.valueOf((param.getMoney() / retio)).setScale(2, RoundingMode.HALF_UP).doubleValue();
            FaUserMoneyEntity um = new FaUserMoneyEntity();
            um.setUserId(param.getUserId());
            um.setMoney(money);
            um.setBefore(faUser.getYongjin());
            um.setAfterMoney(faUser.getYongjin()+money);
            um.setBillCode(param.getBillCode());
            um.setOrderid(param.getOrderId());
            um.setMsg(param.getMsgEumn().getDesc());
            um.setType(param.getAddType());
            um.setCid(param.getCompanyId());
            faUserMoneyDao.insert(um);
            faUserDao.addMoney(param.getUserId(), money);
        }

    }
}
