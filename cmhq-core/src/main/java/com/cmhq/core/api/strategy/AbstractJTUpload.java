package com.cmhq.core.api.strategy;

import com.alibaba.fastjson.JSONObject;
import com.cmhq.core.api.*;
import com.cmhq.core.api.dto.JTCourierOrderDto;
import com.cmhq.core.api.dto.JTUploadData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jiyang.Zheng on 2024/4/7 10:48.
 */
@Component
@Slf4j
public abstract class AbstractJTUpload<Req, T extends JTUploadData> extends AbstractUpload<Req, T> {

    public UploadResult send(T uploadData, UploadCallback callback) {
        String url = getApiHost()+getUploadUrl();
        UploadResult uploadResult = UploadResult.builder().build();
        if (uploadData == null) {
            callback.fail("上传数据不能为空", null);
            return uploadResult.setErrorMsg("上传数据不能为空").setFlag(false);
        }
        String[] split = getToken().split(",");
        String apiAccount;
        String privateKey;
        if (split.length >= 2) {
            apiAccount = split[0];
            privateKey = split[1];
        } else {
            callback.fail("上传令牌异常", null);
            return uploadResult.setErrorMsg("上传令牌异常").setFlag(false);
        }
        try {
            setContentDigest(uploadData);
            String data = JSONObject.toJSONString(uploadData);
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(url+"?uuid=0a29efb3f1344c24851be8d4aae2aa7f");
            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
            httpPost.setHeader("apiAccount", apiAccount);
            httpPost.setHeader("digest", calculateDigest(data,privateKey));
            httpPost.setHeader("timestamp", System.currentTimeMillis()+"");
            List<NameValuePair> params=new ArrayList<>();
            params.add(new BasicNameValuePair("bizContent",data));
            String unKey = uploadData.getUnKey();
            log.info("upload J&T mark: 【{}】， url【{}】，params【{}】，headers【{}】", unKey, url, data, JSONObject.toJSONString(params));
            httpPost.setEntity(new UrlEncodedFormEntity(params, StandardCharsets.UTF_8));
            HttpResponse response = httpClient.execute(httpPost);
            String rsp = EntityUtils.toString(response.getEntity(),StandardCharsets.UTF_8);
            log.info(" upload J&T mark:【{}】， response:【{}】", unKey, rsp);
            if (StringUtils.isNotEmpty(rsp)) {
                JTResponse object = JSONObject.parseObject(rsp,JTResponse.class);
                uploadResult.setJsonMsg(object.getData());
                if (object.isSuccess()) {
                    callback.success(object);
                    return uploadResult.setFlag(true);
                } else {
                    callback.fail(rsp,null);
                    uploadResult.setErrorJsonMsg(rsp).setFlag(false);
                    return uploadResult.setErrorJsonMsg(rsp).setErrorMsg(object.getMsg()).setFlag(false);
                }
            } else {
                callback.fail("对方服务器响应异常", null);
                return uploadResult.setErrorJsonMsg(rsp).setErrorMsg("对方服务器响应异常").setFlag(false);
            }
        } catch (Exception e) {
            callback.fail(e.getMessage(), e);
            log.error("", e);
            return uploadResult.setErrorMsg(e.getMessage()).setFlag(false);
        }

    }

    protected void setContentDigest(T uploadData){
        String customerCode = "";
        String privateKey = "";
        String[] split = getToken().split(",");
        if (split.length >= 3) {
            privateKey = split[1];
            customerCode = split[2];
        }
        //业务digest 签名，Base64(Md5(客户编号+密文+privateKey))，其中密文：MD5(明文密码+jadada236t2) 后大写
        //明文密码
        String pwd ="H5CD3zE6" ;
        //密文
        String secretText = (customerCode+calculateDigest(pwd,"jadada236t2")).toUpperCase();
        String digest = calculateDigest(secretText,privateKey);

        uploadData.setCustomerCode(customerCode);
        uploadData.setDigest(digest);
    }

}
