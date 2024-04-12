package com.cmhq.core.api.strategy.apipush;

import com.alibaba.fastjson.JSONObject;
import com.cmhq.core.api.UploadData;
import com.cmhq.core.dao.FaCourierOrderDao;
import com.cmhq.core.dao.FaCourierOrderExtDao;
import com.cmhq.core.dao.FaUploadLogDao;
import com.cmhq.core.model.FaCourierOrderEntity;
import com.cmhq.core.model.UploadLogEntity;
import com.cmhq.core.service.FaCompanyMoneyService;
import com.cmhq.core.util.SpringApplicationUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.Date;

/**
 * Created by Jiyang.Zheng on 2024/4/11 18:17.
 */
@Slf4j
@Component
public abstract class AbstractApiPush<Req extends UploadData> implements ApiPush<Req> {
    @Autowired
    protected FaCompanyMoneyService faCompanyMoneyService;
    @Autowired
    protected FaCourierOrderDao faCourierOrderDao;
    @Autowired
    protected  FaUploadLogDao logDao;
    @Transactional
    @Override
    public void pushHandle(Req req) {
        //请求日志写入
        log.info("获取 【{}】 params【{}】",supports().getDesc(), JSONObject.toJSONString(req));
        UploadLogEntity entity = new UploadLogEntity();
        entity.setUploadType(supports().getType());
        entity.setUploadCode(req.getUnKey());
        entity.setMsg(JSONObject.toJSONString(req));
        entity.setCreateTime(new Date());
        try {

            doPushHandle(req);
            afterHandle(req);
        }catch (Exception e){
            log.error("【{}】 异常",supports().getDesc(),e);
            entity.setErrorMsg(e.getMessage());
        }finally {
            logDao.insert(entity);
        }



    }

    /**
     *
     * @param req
     */
    protected abstract void doPushHandle(Req req);

    protected abstract void afterHandle(Req req);

    protected abstract FaCourierOrderEntity getFaCourierOrder(Req req);

}