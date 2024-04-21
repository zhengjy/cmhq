package com.cmhq.core.controller;

import com.alibaba.fastjson.JSONObject;
import com.yl.jms.sdk.JtExpressApi;
import com.yl.jms.sdk.auth.ClientConfiguration;
import com.yl.jms.sdk.client.JtExpressApiOperator;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jiyang.Zheng on 2024/4/21 18:15.
 */
public class SdkTestDemo {
    public static final String apiAccount="178337126125932605";
    public static final String privateKey="0258d71b55fc45e3ad7a7f38bf4b201a";
    public static final String customerCode="J0086474299";
    public static final String orderPassword="H5CD3zE6";
    //用户要调用的接口地址
    public static final String url = "https://uat-openapi.jtexpress.com.cn/webopenplatformapi/api/location/getLocation";
    public static void main(String[] args) throws IOException {
        //创建授权用户实体
        ClientConfiguration clientConfiguration = new ClientConfiguration(apiAccount,privateKey);
        //当调用postByCustom方法时需要customerCode和orderPassword
        clientConfiguration.setCustomerCode(customerCode);
        clientConfiguration.setCustomerPwd(orderPassword);
        JtExpressApi jtExpressApi = new JtExpressApiOperator(clientConfiguration);
        //把接口需要的参数封装到map
        Map<String, Object> map = new HashMap<>();
        map.put("countryCode","CHN");
        //调用执行器,调用接口方法
        JSONObject location = jtExpressApi.post(map,url);
        System.out.println(location);
    }

}
