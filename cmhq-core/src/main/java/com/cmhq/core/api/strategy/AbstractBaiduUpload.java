package com.cmhq.core.api.strategy;

import com.alibaba.fastjson.JSONObject;
import com.cmhq.core.api.StoResponse;
import com.cmhq.core.api.UploadCallback;
import com.cmhq.core.api.UploadData;
import com.cmhq.core.api.UploadResult;
import lombok.extern.slf4j.Slf4j;

import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jiyang.Zheng on 2024/4/7 10:48.
 */
@Component
@Slf4j
public abstract class AbstractBaiduUpload<Req, T extends UploadData> extends AbstractUpload<Req, T> {


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

    }
    static final OkHttpClient HTTP_CLIENT = new OkHttpClient().newBuilder().build();

    public static void main(String []args) throws IOException{
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "");
        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/rpc/2.0/nlp/v1/address?access_token=" + getAccessToken())
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build();
        Response response = HTTP_CLIENT.newCall(request).execute();
        System.out.println(response.body().string());

    }


    /**
     * 从用户的AK，SK生成鉴权签名（Access Token）
     *
     * @return 鉴权签名（Access Token）
     * @throws IOException IO异常
     */
    private String getAccessToken() throws IOException {
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
                .url("https://aip.baidubce.com/oauth/2.0/token")
                .method("POST", body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();
        Response response = HTTP_CLIENT.newCall(request).execute();
        return  JSONObject.parseObject(response.body().string()).getString("access_token");
    }

}
