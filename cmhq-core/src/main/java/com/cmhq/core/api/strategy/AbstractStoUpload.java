package com.cmhq.core.api.strategy;

import com.alibaba.fastjson.JSONObject;
import com.cmhq.core.api.UploadCallback;
import com.cmhq.core.api.UploadData;
import com.cmhq.core.api.StoResponse;
import com.cmhq.core.api.UploadResult;
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

    /**
     * 执行上传
     *
     * @param uploadData
     * @return
     */
    @Override
    protected <T extends UploadData> int[] doUpload(List<T> uploadData, UploadCallback callback) {

        //上传
        int successNum = 0;
        int failNum = 0;
        for (T data : uploadData) {
            UploadResult ur = send(data, callback);
            if (!ur.getFlag()) {
                failNum++;
            } else {
                successNum++;
            }
//            uploadResultHandle(Arrays.asList(data), ur);
        }
        return new int[]{successNum, failNum};

    }


    public <T extends UploadData> UploadResult send(T uploadData, UploadCallback callback) {
        String url = getApiHost();
        UploadResult uploadResult = UploadResult.builder().build();
        if (uploadData == null) {
            callback.fail("上传数据不能为空", null);
            return uploadResult.setErrorMsg("上传数据不能为空").setFlag(false);
        }
        String[] split = getToken().split(",");
        String from_appkey;
        String from_code;
        String to_appkey;
        String to_code;
        String clientSecret;
        if (split.length == 5) {
            from_appkey = split[0];
            from_code = split[1];
            to_appkey = split[2];
            to_code = split[3];
            clientSecret = split[4];
        } else {
            callback.fail("上传令牌异常", null);
            return uploadResult.setErrorMsg("上传令牌异常").setFlag(false);
        }

        try {
            String data = JSONObject.toJSONString(uploadData);
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(url);
            httpPost.addHeader("Content-Type", " x-www-form-urlencoded");
            httpPost.addHeader("data_digest", calculateDigest(data,clientSecret));
            httpPost.addHeader("api_name", getUploadUrl());
            httpPost.addHeader("from_appkey", from_appkey);
            httpPost.addHeader("from_code", from_code);
            httpPost.addHeader("to_appkey", to_appkey);
            httpPost.addHeader("to_code", to_code);
            httpPost.addHeader("content", data);

            String unKey = uploadData.getUnKey();
            log.info("upload sto mark: 【{}】， url【{}】，params【{}】，headers【{}】", unKey, url, data, JSONObject.toJSONString(httpPost.getAllHeaders()));
            httpPost.setEntity(new StringEntity(data, StandardCharsets.UTF_8));
            HttpResponse response = httpClient.execute(httpPost);
            String rsp = EntityUtils.toString(response.getEntity());
            log.info(" upload sto mark:【{}】， response:【{}】", unKey, rsp);
            if (StringUtils.isNotEmpty(rsp)) {
                uploadResult.getJsonMsg().put(unKey,rsp);
                StoResponse object = JSONObject.parseObject(rsp,StoResponse.class);
                if (object.isSuccess()) {
                    callback.success(object);
                    return uploadResult.setFlag(true);
                } else {
                    callback.fail(rsp,null);
                    uploadResult.setErrorJsonMsg(rsp).setFlag(false);
                    return uploadResult.setErrorMsg(object.getErrorMsg()).setFlag(false);
                }
            } else {
                callback.fail("对方服务器响应异常", null);
                return uploadResult.setErrorMsg("对方服务器响应异常").setFlag(false);
            }
        } catch (Exception e) {
            callback.fail(e.getMessage(), e);
            log.error("", e);
            return uploadResult.setErrorMsg(e.getMessage()).setFlag(false);
        }
    }
    /**
     * 计算签名值
     *
     * @param content  请求报文体
     * @param secretKey    配置的私钥
     * @return
     */
    public static String calculateDigest(String content, String secretKey) {
        String text = content + secretKey;
        byte[] md5 = DigestUtils.md5(text);
        return Base64.encodeBase64String(md5);
    }

    /**
     * 认证获取授权token
     *
     * @return
     */
    public String authPost(String account,
                           String clientKey,
                           String clientSecret, UploadCallback callback) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String timestamp = String.valueOf(System.currentTimeMillis());
        String responseStr = "";
        try {
            String url = "";
            HttpPost httpPost = new HttpPost(url);
            httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");
            httpPost.addHeader("clientKey", clientKey);
            httpPost.addHeader("timestamp", timestamp);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("clientKey", clientKey);
            jsonObject.put("account", account);
            jsonObject.put("timestamp", timestamp);
            String encrypt = AesUtil.encryptToBase64(jsonObject.toJSONString(), clientSecret);
            log.info("shanghai auth send url【{}】  param【{}】，headers【{}】，encrypy【{}】", url, jsonObject.toJSONString(), JSONObject.toJSONString(httpPost.getAllHeaders()), encrypt);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("data", encrypt));
            httpPost.setEntity(new UrlEncodedFormEntity(params, StandardCharsets.UTF_8));

            HttpResponse response = httpClient.execute(httpPost);
            responseStr = EntityUtils.toString(response.getEntity());
            log.info("shanghai auth response 【{}】", responseStr);
            if (response != null) {
                int statusCode = response.getStatusLine().getStatusCode();
                if (HttpStatus.SC_OK == statusCode) {
                    StoResponse parseObject = JSONObject.parseObject(responseStr, StoResponse.class);
                    return null;
                }
            }
        } catch (Exception e) {
            String msg = "签名发生异常，响应值为【" + responseStr + "】";
            callback.fail(msg, e);
            throw new RuntimeException(msg, e);
        }
        return null;
    }

}
