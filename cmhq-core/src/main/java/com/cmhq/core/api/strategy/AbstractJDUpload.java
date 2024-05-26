package com.cmhq.core.api.strategy;

import com.alibaba.fastjson.JSONObject;
import com.cmhq.core.api.UploadCallback;
import com.cmhq.core.api.UploadResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import com.lop.open.api.sdk.DefaultDomainApiClient;
import com.lop.open.api.sdk.plugin.LopPlugin;
import com.lop.open.api.sdk.plugin.factory.OAuth2PluginFactory;

/**
 * Created by Jiyang.Zheng on 2024/4/7 10:48.
 */
@Component
@Slf4j
public abstract class AbstractJDUpload<Req, T extends JDUploadData> extends AbstractUpload<Req, T> {

    @Override
    protected String getUploadUrl() {
        return null;
    }

    public UploadResult send(T uploadData, UploadCallback callback) {
        UploadResult uploadResult = UploadResult.builder().build();
        if (uploadData == null) {
            callback.fail("上传数据不能为空", null);
            return uploadResult.setErrorMsg("上传数据不能为空").setFlag(false);
        }
        try {
            log.info("发送京东 【{}】 params【{}】",supports().getDesc(),JSONObject.toJSONString(uploadData));
            JDResponse rsp = JSONObject.parseObject(JSONObject.toJSONString(doSend(uploadData)),JDResponse.class);
            if (rsp != null) {
                if (rsp.getSuccess()) {
                    uploadResult.setJsonMsg(jsonMsgHandle(rsp.getData()));
                    callback.success(rsp);
                    return uploadResult.setFlag(true);
                } else {
                    callback.fail(rsp,null);
                    uploadResult.setErrorJsonMsg(rsp.getMsg()+rsp.getMsg()).setFlag(false);
                    return uploadResult.setErrorJsonMsg(rsp.getMsg()+rsp.getMsg()).setErrorMsg(rsp.getMsg()+rsp.getMsg()).setFlag(false);
                }
            } else {
                callback.fail("对方服务器响应异常", null);
                return uploadResult.setErrorJsonMsg(rsp.getMsg()).setErrorMsg("对方服务器响应异常").setFlag(false);
            }
        } catch (Exception e) {
            callback.fail(e.getMessage(), e);
            log.error("", e);
            return uploadResult.setErrorMsg(e.getMessage()).setFlag(false);
        }

    }

    protected DefaultDomainApiClient geDefaultDomainApiClient(){
        //设置接口域名(有的对接方案同时支持生产和沙箱环境，有的仅支持生产，具体以对接方案中的【API文档-请求地址】为准)，生产域名：https://api.jdl.com 预发环境域名：https://uat-api.jdl.com
        //DefaultDomainApiClient对象全局只需要创建一次
        DefaultDomainApiClient client = new DefaultDomainApiClient(getApiHost(),500,15000);
        return client;
    }
    protected LopPlugin getProduceLopPlugin(){
        String[] split = getToken().split(",");
        String appKey;
        String appSecret;
        String accessToken = "";
        appKey = split[0];
        appSecret = split[1];
        accessToken = split[2];
        //使用开放平台合作伙伴应用调用接口
//            LopPlugin lopPlugin = OAuth2PluginFactory.produceLopPlugin("bae34******************8fd", "661e4d**********************ec", "");
//            request.addLopPlugin(lopPlugin);

        //使用JOS应用调用物流开放平台接口
//            request.setUseJosAuth(true);
//            LopPlugin lopPlugin = OAuth2PluginFactory.produceLopPlugin("DE79844E3***********43236CC", "7b01ff52c2********7b661448", "b89114***************d4e9da950m2u");
//            request.addLopPlugin(lopPlugin);
        return OAuth2PluginFactory.produceLopPlugin(appKey, appSecret, accessToken);
    }

    protected abstract Object doSend(T uploadData) throws Exception;

    protected Object jsonMsgHandle(Object jsonMsg) {
        return jsonMsg;
    }

    protected boolean isSuccess(String code ){
        return StringUtils.equals(code,"0");
    }

    protected String getCustomerCode(){
        String[] split = getToken().split(",");
        return split[3];
    }

}
