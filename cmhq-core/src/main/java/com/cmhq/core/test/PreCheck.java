package com.cmhq.core.test;
import com.lop.open.api.sdk.DefaultDomainApiClient;
import com.lop.open.api.sdk.LopException;
import com.lop.open.api.sdk.domain.ECAP.CommonCreateOrderApi.commonCheckPreCreateOrderV1.CommonCreateOrderRequest;
import com.lop.open.api.sdk.domain.ECAP.CommonCreateOrderApi.commonCheckPreCreateOrderV1.CommonProductInfo;
import com.lop.open.api.sdk.domain.ECAP.CommonCreateOrderApi.commonCheckPreCreateOrderV1.Contact;
import com.lop.open.api.sdk.plugin.LopPlugin;
import com.lop.open.api.sdk.plugin.factory.OAuth2PluginFactory;
import com.lop.open.api.sdk.request.ECAP.EcapV1OrdersPrecheckLopRequest;
import com.lop.open.api.sdk.response.ECAP.EcapV1OrdersPrecheckLopResponse;

public class PreCheck {

    public static void main(String[] args) {
        //设置接口域名(有的对接方案同时支持生产和沙箱环境，有的仅支持生产，具体以对接方案中的【API文档-请求地址】为准)，生产域名：https://api.jdl.com 预发环境域名：https://uat-api.jdl.com
        //DefaultDomainApiClient对象全局只需要创建一次
        DefaultDomainApiClient client = new DefaultDomainApiClient("https://api.jdl.com", 500, 15000);

        //入参对象（请记得更换为自己要使用的接口入参对象）
        EcapV1OrdersPrecheckLopRequest request = new EcapV1OrdersPrecheckLopRequest();

        //设置入参（请记得更换为自己要使用的接口入参）
        Contact senderContact = new Contact();
        senderContact.setFullAddress("河北省廊坊市广阳区万庄镇中心小学");
        Contact receiverContact = new Contact();
        receiverContact.setFullAddress("河北省廊坊市广阳区万庄镇中心小学");
        CommonCreateOrderRequest requestDTO = new CommonCreateOrderRequest();
        requestDTO.setOrderOrigin(1);
        requestDTO.setCustomerCode("010K****20");
        CommonProductInfo productInfo = new CommonProductInfo();
        productInfo.setProductCode("ed-m-0001");
        requestDTO.setProductsReq(productInfo);
        requestDTO.setReceiverContact(receiverContact);
        requestDTO.setSenderContact(senderContact);
        request.setRequest(requestDTO);

        //设置插件，必须的操作，不同类型的应用入参不同，请看入参注释，公共参数按顺序分别为AppKey、AppSecret、AccessToken
        //使用开放平台ISV/自研商家应用调用接口
        LopPlugin lopPlugin = OAuth2PluginFactory.produceLopPlugin("eb8bb6********************b3604", "333a******************1170", "11************11");
        request.addLopPlugin(lopPlugin);

        //使用开放平台合作伙伴应用调用接口
//            LopPlugin lopPlugin = OAuth2PluginFactory.produceLopPlugin("bae34******************8fd", "661e4d**********************ec", "");
//            request.addLopPlugin(lopPlugin);

        //使用JOS应用调用物流开放平台接口
//            request.setUseJosAuth(true);
//            LopPlugin lopPlugin = OAuth2PluginFactory.produceLopPlugin("DE79844E3***********43236CC", "7b01ff52c2********7b661448", "b89114***************d4e9da950m2u");
//            request.addLopPlugin(lopPlugin);

        EcapV1OrdersPrecheckLopResponse response = null;
        try {
            response = client.execute(request);
        } catch (LopException e) {
            throw new RuntimeException(e);
        }
        System.out.println(response.getMsg());
    }
}
