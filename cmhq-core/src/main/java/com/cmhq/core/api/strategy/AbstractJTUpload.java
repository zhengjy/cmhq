package com.cmhq.core.api.strategy;

import com.alibaba.fastjson.JSONObject;
import com.cmhq.core.api.*;
import com.cmhq.core.api.dto.JTUploadData;
import com.yl.jms.sdk.JtExpressApi;
import com.yl.jms.sdk.auth.ClientConfiguration;
import com.yl.jms.sdk.client.JtExpressApiOperator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Map;

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
        String customerCode;
        String orderPassword;
        if (split.length >= 4) {
            apiAccount = split[0];
            privateKey = split[1];
            customerCode = split[2];
            orderPassword = split[3];
        } else {
            callback.fail("上传令牌异常", null);
            return uploadResult.setErrorMsg("上传令牌异常").setFlag(false);
        }
        try {
            setContentDigest( uploadData);
            //创建授权用户实体
            ClientConfiguration clientConfiguration = new ClientConfiguration(apiAccount,privateKey);
            //当调用postByCustom方法时需要customerCode和orderPassword
            clientConfiguration.setCustomerCode(customerCode);
            clientConfiguration.setCustomerPwd(orderPassword);
            JtExpressApi jtExpressApi = new JtExpressApiOperator(clientConfiguration);
            //把接口需要的参数封装到map
            String data = JSONObject.toJSONString(uploadData);
            Map map = JSONObject.parseObject(JSONObject.toJSONString(uploadData),Map.class);
            //调用执行器,调用接口方法
            String unKey = uploadData.getUnKey();
            log.info("upload J&T mark: 【{}】， url【{}】，params【{}】，obj【{}】", unKey, url, data, jtExpressApi.toString());
            JSONObject rsp = jtExpressApi.post(map,url);
            log.info(" upload J&T mark:【{}】， response:【{}】", unKey, rsp);
            if (rsp != null) {
                JTResponse object = JSONObject.parseObject(rsp.toJSONString(),JTResponse.class);
                uploadResult.setJsonMsg(jsonMsgHandle(object.getData()));
                if (object.isSuccess()) {
                    callback.success(object);
                    return uploadResult.setFlag(true);
                } else {
                    callback.fail(rsp,null);
                    uploadResult.setErrorJsonMsg(rsp.toJSONString()).setFlag(false);
                    return uploadResult.setErrorJsonMsg(rsp.toJSONString()).setErrorMsg(object.getMsg()).setFlag(false);
                }
            } else {
                callback.fail("对方服务器响应异常", null);
                return uploadResult.setErrorJsonMsg(rsp.toJSONString()).setErrorMsg("对方服务器响应异常").setFlag(false);
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

    protected void setContentDigest(T uploadData){
        String[] split = getToken().split(",");
        String apiAccount;
        String privateKey;
        String customerCode;
        String orderPassword;
        if (split.length >= 4) {
            apiAccount = split[0];
            privateKey = split[1];
            customerCode = split[2];
            orderPassword = split[3];
        }else {
            return;
        }
        //业务digest 签名，Base64(Md5(客户编号+密文+privateKey))，其中密文：MD5(明文密码+jadada236t2) 后大写
        //明文密码
        String pwd =orderPassword ;
        //密文
//        String secretText = (customerCode+calculateDigest(pwd,"jadada236t2")).toUpperCase();
//        String digest = calculateDigest(secretText,privateKey);


        String salt = "jadada236t2";
        String MD5Password = null;
        try {
            MD5Password = MD5Encrypt(pwd + salt).toUpperCase();
            String dataToEncode = customerCode + MD5Password + privateKey;
            String encodedData = base64Encode(MD5Encrypt(dataToEncode));
            uploadData.setCustomerCode(customerCode);
            uploadData.setDigest(encodedData);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private static String MD5Encrypt(String data) throws Exception {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(data.getBytes("UTF-8"));
        byte[] digest = md5.digest();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < digest.length; i++) {
            String hex = Integer.toHexString(0xff & digest[i]);
            if(hex.length() == 1) sb.append('0');
            sb.append(hex);
        }
        return sb.toString();
    }

    private static String base64Encode(String data) throws UnsupportedEncodingException {
        return Base64.getEncoder().encodeToString(data.getBytes("UTF-8"));
    }

}
