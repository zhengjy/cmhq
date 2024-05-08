package com.cmhq.core.api.dto.request;

import com.cmhq.core.api.UploadData;
import io.swagger.models.auth.In;
import lombok.Data;

import java.util.List;

/**
 * 物流轨迹回传
 * Created by Jiyang.Zheng on 2024/4/10 21:11.
 */
@Data
public class JDPushTraceDto extends UploadData {



    /**路由 省*/
    private String routeProvinceName;
    /**
     机构编码，操作该全程跟踪的站点ID，长度1-10*/
    private String operateSiteId;
    /**商家订单号，长度1-50
     */
    private String orderId;
    /**节点名称，长度1-20
     */
    private String operationTitle;
    /***/
    private String routeDistrictName;
    /*一级分类名称，长度1-20
     **/
    private String categoryName;
    /**操作时间，格式：yyyy-MM-dd HH:mm:ss，字段长度：18
     */
    private String operationTime;
    /***/
    private String routeAddress;
    /***/
    private String routeCityName;
    /**京东物流运单号，长度1-50
     */
    private String waybillCode;
    /**节点编码，长度1-10
     */
    private String state;
    /***/
    private String scanType;
    /**一级分类编码，长度1-10
     */
    private Integer category;
    /***/
    private String operateSite;
    /***/
    private String operationRemark;
    /**取消原因；在state=200052「终止揽收」节点返回，长度1-100*/
    private String cancelReason;

    @Override
    public String getUnKeyValue() {
        return waybillCode;
    }
}
