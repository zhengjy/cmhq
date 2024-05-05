package com.cmhq.core.api.strategy.apipush;

import com.alibaba.fastjson.JSONObject;
import com.cmhq.core.api.JDResponse;
import com.cmhq.core.api.JTResponse;
import com.cmhq.core.api.StoResponse;
import com.cmhq.core.api.UploadData;
import com.cmhq.core.dao.FaCourierOrderDao;
import com.cmhq.core.dao.FaCourierOrderExtDao;
import com.cmhq.core.dao.FaUploadLogDao;
import com.cmhq.core.model.FaCourierOrderEntity;
import com.cmhq.core.model.UploadLogEntity;
import com.cmhq.core.service.FaCompanyMoneyService;
import com.cmhq.core.service.FaCourierOrderService;
import com.cmhq.core.util.SpringApplicationUtils;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.APIResponse;
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
    protected FaCourierOrderService faCourierOrderService;
    @Autowired
    protected  FaUploadLogDao logDao;
    @Transactional
    @Override
    public JTResponse jtPushHandle(Req req) {
        JTResponse rsp = new JTResponse();
        try {
            log(req);
            doPushHandle(req);
            afterHandle(req);
            rsp.setCode("1");
            rsp.setMsg("success");
            rsp.setData("SUCCESS");
        }catch (RuntimeException e){
            rsp.setCode("0");
            rsp.setMsg(e.getMessage());
            log.error("接收【{}】异常, params{}",supports().getDesc(), JSONObject.toJSONString(req),e);
        }catch (Exception e){
            rsp.setCode("0");
            rsp.setMsg("发生系统未知异常");
            log.error("接收【{}】异常, params{}",supports().getDesc(), JSONObject.toJSONString(req),e);
        }
        return rsp;
    }
    @Transactional
    @Override
    public JDResponse jdPushHandle(Req req) {

        JDResponse rsp = new JDResponse();
        try {
            log(req);
            doPushHandle(req);
            afterHandle(req);
            rsp.setCode("200");

        }catch (RuntimeException e){
            rsp.setCode("500");
            rsp.setMsg(e.getMessage());
            log.error("接收【{}】异常, params{}",supports().getDesc(), JSONObject.toJSONString(req),e);
        }catch (Exception e){
            rsp.setCode("500");
            rsp.setMsg("发生系统未知异常");
            log.error("接收【{}】异常, params{}",supports().getDesc(), JSONObject.toJSONString(req),e);
        }
        return rsp;
    }
    @Transactional
    @Override
    public StoResponse stoPushHandle(Req req) {

        StoResponse rsp = new StoResponse();
        try {
            log(req);
            doPushHandle(req);
            afterHandle(req);
            rsp.setSuccess(true);

        }catch (RuntimeException e){
            rsp.setSuccess(false);
            rsp.setErrorMsg(e.getMessage());
            log.error("接收【{}】异常, params{}",supports().getDesc(), JSONObject.toJSONString(req),e);
        }catch (Exception e){
            rsp.setSuccess(false);
            rsp.setErrorMsg("发生系统未知异常");
            log.error("接收【{}】异常, params{}",supports().getDesc(), JSONObject.toJSONString(req),e);
        }
        return rsp;
    }

    private void  log(Req req){
        //请求日志写入
        log.info("获取 【{}】 params【{}】",supports().getDesc(), JSONObject.toJSONString(req));
        UploadLogEntity entity = new UploadLogEntity();
        entity.setUploadType(supports().getType());
        entity.setUploadCode(req.getUnKey());
        entity.setMsg(JSONObject.toJSONString(req));
        entity.setCreateTime(new Date());
        logDao.insert(entity);
    }


    /**
     *
     * @param req
     */
    protected abstract void doPushHandle(Req req);

    protected abstract void afterHandle(Req req);

    protected abstract FaCourierOrderEntity getFaCourierOrder(Req req);

}
