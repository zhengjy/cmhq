package com.cmhq.core.util;
import com.alibaba.fastjson.JSONObject;
import com.yl.jms.sdk.JtExpressApi;
import com.yl.jms.sdk.auth.ClientConfiguration;
import com.yl.jms.sdk.client.JtExpressApiOperator;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Map;

/**
 * Created by Jiyang.Zheng on 2024/4/22 17:44.
 */
@Slf4j
public class SignDemoUtils {
    /**
     * @param account
     * @param pwd
     * @param key
     * @return : java.lang.String
     * @description : 业务参数签名
     */
    public static String accountSign(String account, String pwd, String key) throws NoSuchAlgorithmException {
        String strToDigest = account + pwd + key;
        log.info("待签名字符串：{}", strToDigest);
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(strToDigest.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(md.digest());
    }

    public static final String apiAccount="178337126125932605";
    public static final String privateKey="0258d71b55fc45e3ad7a7f38bf4b201a";
    public static final String customerCode="J0086474299";
    public static final String orderPassword="H5CD3zE6";

    /**
     * @param params
     * @param key
     * @return : java.lang.String
     * @description : Headers签名
     */
    public static String headersSign(Object params, String key) throws NoSuchAlgorithmException {
        String strToDigest = params + key;
        log.info("待签名字符串：{}", strToDigest);
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(strToDigest.getBytes(Charset.forName("UTF-8")));
        String dataDigest = Base64.getEncoder().encodeToString(md.digest());
        return dataDigest;
    }

    public static String MD5Encrypt(String data) throws Exception {
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

    public static final String url = "https://uat-openapi.jtexpress.com.cn/webopenplatformapi/api/order/addOrder";
    public static void main(String[] args) throws Exception {
        String jsonstr = "{\"network\":null,\"txlogisticId\":\"TEST20220704210006\",\"expressType\":\"EZ\",\"orderType\":\"1\",\"serviceType\":\"01\",\"deliveryType\":\"06\",\"payType\":\"CC_CASH\",\"sender\":{\"name\":\"小九\",\"company\":null,\"postCode\":null,\"mailBox\":null,\"mobile\":\"15546168286\",\"phone\":\"\",\"countryCode\":\"CHN\",\"prov\":\"上海\",\"city\":\"上海市\",\"area\":\"青浦区\",\"town\":null,\"street\":null,\"address\":\"庆丰三路28号\"},\"receiver\":{\"name\":\"田丽\",\"company\":null,\"postCode\":null,\"mailBox\":null,\"mobile\":\"13766245825\",\"phone\":\"\",\"countryCode\":\"CHN\",\"prov\":\"上海\",\"city\":\"上海市\",\"area\":\"嘉定区\",\"town\":null,\"street\":null,\"address\":\"站前西路永利酒店斜对面童装店\"},\"sendStartTime\":\"2022-07-04 09:00:00\",\"sendEndTime\":\"2022-07-04 18:00:00\",\"goodsType\":\"bm000006\",\"length\":0,\"width\":0,\"height\":0,\"weight\":\"0.02\",\"totalQuantity\":0,\"itemsValue\":null,\"priceCurrency\":null,\"offerFee\":null,\"remark\":null,\"items\":[{\"itemType\":null,\"itemName\":\"衣帽鞋服\",\"chineseName\":null,\"englishName\":null,\"number\":1,\"itemValue\":null,\"priceCurrency\":\"RMB\",\"desc\":null,\"itemUrl\":null}],\"customsInfo\":null,\"postSiteCode\":null,\"postSiteName\":null,\"postSiteAddress\":null,\"realName\":null}";
        Map map = JSONObject.parseObject(jsonstr,Map.class);
        String salt = "jadada236t2";
        String MD5Password = null;
        //签名，Base64(Md5(客户编号+密文+privateKey))，其中密文：MD5(明文密码+jadada236t2) 后大写
        //"customerCode":"J0086474299","digest":"qonqb4O1eNr6VCWS07Ieeg==",
        MD5Password = MD5Encrypt(orderPassword + salt).toUpperCase();
        log.info("accountSign:{}", accountSign(customerCode,MD5Password,privateKey));

        String accountSign = accountSign(customerCode,MD5Password,privateKey);
        map.put("customerCode",customerCode);
        map.put("digest",accountSign);

        //创建授权用户实体
        ClientConfiguration clientConfiguration = new ClientConfiguration(apiAccount,privateKey);
        //当调用postByCustom方法时需要customerCode和orderPassword
        clientConfiguration.setCustomerCode(customerCode);
        clientConfiguration.setCustomerPwd(orderPassword);
        JtExpressApi jtExpressApi = new JtExpressApiOperator(clientConfiguration);
        //把接口需要的参数封装到map
        //调用执行器,调用接口方法
        JSONObject location = jtExpressApi.post(map,url);
        System.out.println(location);
    }

}
