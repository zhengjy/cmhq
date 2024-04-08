package com.cmhq.core.api.strategy;

import com.alibaba.fastjson.JSONObject;
import com.cmhq.core.api.UploadCallback;
import com.cmhq.core.api.UploadData;
import com.cmhq.core.api.UploadResult;
import lombok.extern.slf4j.Slf4j;

import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Created by Jiyang.Zheng on 2024/4/7 10:48.
 */
@Component
@Slf4j
public abstract class AbstractBaiduUpload<Req, T extends UploadData> extends AbstractUpload<Req, T> {

    public UploadResult send(T uploadData, UploadCallback callback) {
        OkHttpClient HTTP_CLIENT = new OkHttpClient().newBuilder().build();
        String url = getApiHost() + getUploadUrl();
        UploadResult uploadResult = UploadResult.builder().build();
        try {
            String data = JSONObject.toJSONString(uploadData);
            String unKey = uploadData.getUnKey();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, data);
            Request request = new Request.Builder()
                    .url(url + getAccessToken())
//                    .url("https://aip.baidubce.com/rpc/2.0/nlp/v1/address?access_token=" + getAccessToken())
                    .method("POST", body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .build();
            log.info("get baidu mark: 【{}】， url【{}】，params【{}】，headers【{}】", unKey, url, data, JSONObject.toJSONString(request.headers()));
            Response response = HTTP_CLIENT.newCall(request).execute();

            String rsp = response.body().string();
            log.info(" get baidu mark:【{}】， response:【{}】", unKey, rsp);
            if (StringUtils.isNotEmpty(rsp)) {
                uploadResult.setJsonMsg(rsp);
                uploadResult.setFlag(true);
                callback.success(rsp);
                return uploadResult;
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
     * 从用户的AK，SK生成鉴权签名（Access Token）
     *
     * @return 鉴权签名（Access Token）
     * @throws IOException IO异常
     */
    private String getAccessToken() throws IOException {
        OkHttpClient HTTP_CLIENT = new OkHttpClient().newBuilder().build();
        String[] split = getToken().split(",");
        String appkey = null;
        String secretkey = null;
        if (split.length == 2) {
            appkey = split[0];
            secretkey = split[1];
        }
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");

        RequestBody body = RequestBody.create(mediaType, "grant_type=client_credentials&client_id=" + appkey
                + "&client_secret=" + secretkey);
        Request request = new Request.Builder()
                .url(getApiHost()+"/oauth/2.0/token")
                .method("POST", body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();
        Response response = HTTP_CLIENT.newCall(request).execute();
        return  JSONObject.parseObject(response.body().string()).getString("access_token");
    }

}
