package com.cmhq.core.api.strategy;

import com.alibaba.fastjson.JSONObject;
import com.cmhq.core.api.UploadCallback;
import com.cmhq.core.api.UploadData;
import com.cmhq.core.api.StoResponse;
import com.cmhq.core.api.UploadResult;
import com.cmhq.core.api.dto.BaseStoDto;
import lombok.extern.slf4j.Slf4j;
import net.dreamlu.mica.core.utils.AesUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;
import  org.apache.commons.codec.binary.Base64;

import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Created by Jiyang.Zheng on 2024/4/7 10:48.
 */
@Component
@Slf4j
public abstract class AbstractStoUpload<Req, T extends UploadData> extends AbstractUpload<Req, T> {



    public  UploadResult send(T uploadData, UploadCallback callback) {
        String url = getApiHost();
        UploadResult uploadResult = UploadResult.builder().build();
        if (uploadData == null) {
            callback.fail("上传数据不能为空", null);
            return uploadResult.setErrorMsg("上传数据不能为空").setFlag(false);
        }
        String[] split = getToken().split(",");
        String from_appkey;
        String from_code;
        String to_appkey = getToCode();
        String to_code = getToCode();
        String clientSecret;
        if (split.length >= 3) {
            from_appkey = split[0];
            from_code = split[1];
            clientSecret = split[2];
        } else {
            callback.fail("上传令牌异常", null);
            return uploadResult.setErrorMsg("上传令牌异常").setFlag(false);
        }

        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
            String data = JSONObject.toJSONString(uploadData);
            List<NameValuePair> params=new ArrayList<>();
            params.add(new BasicNameValuePair("data_digest",calculateDigest(data,clientSecret)));
            params.add(new BasicNameValuePair("api_name",getUploadUrl()));
            params.add(new BasicNameValuePair("from_appkey",from_appkey));
            params.add(new BasicNameValuePair("from_code",from_code));
            params.add(new BasicNameValuePair("to_appkey",to_appkey));
            params.add(new BasicNameValuePair("to_code",to_code));
            params.add(new BasicNameValuePair("content",data));

            String unKey = uploadData.getUnKey();
            log.info("upload sto mark: 【{}】， url【{}】，params【{}】，headers【{}】", unKey, url, data, JSONObject.toJSONString(params));
            httpPost.setEntity(new UrlEncodedFormEntity(params, StandardCharsets.UTF_8));
            HttpResponse response = httpClient.execute(httpPost);
            String rsp = EntityUtils.toString(response.getEntity(),StandardCharsets.UTF_8);
            log.info(" upload sto mark:【{}】， response:【{}】", unKey, rsp);
            if (StringUtils.isNotEmpty(rsp)) {
                StoResponse object = JSONObject.parseObject(rsp,StoResponse.class);
                uploadResult.setJsonMsg(jsonMsgHandle(object.getData()));
                if (object.isSuccess()) {
                    callback.success(object);
                    return uploadResult.setFlag(true);
                } else {
                    callback.fail(rsp,null);
                    uploadResult.setErrorJsonMsg(rsp).setFlag(false);
                    return uploadResult.setErrorJsonMsg(rsp).setErrorMsg(object.getErrorMsg()).setFlag(false);
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



    protected Object jsonMsgHandle(Object jsonMsg) {
        return jsonMsg;
    }

    protected abstract String getToCode();
}
