package com.cmhq.core.service;

import com.cmhq.core.model.FaCourierOrderEntity;
import com.cmhq.core.model.dto.FaCostFreight;
import com.cmhq.core.model.dto.FreightChargeDto;
import com.cmhq.core.model.param.CourierOrderQuery;
import com.cmhq.core.model.param.ShareCreateCourier;
import me.zhengjie.QueryResult;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * Created by Jiyang.Zheng on 2024/4/6 13:59.
 */
public interface FaCourierOrderService {

    /**
     * 查询数据分页
     * @return Map<String,Object>
     */
    QueryResult<FaCourierOrderEntity> queryAll(CourierOrderQuery query);


    Integer create(FaCourierOrderEntity entity);

    /**
     * 获取物流费用
     * @param entity
     * @return
     */
    FreightChargeDto getCourierFreightCharge(FaCourierOrderEntity entity,Integer retio);
    /**
     * 获取所有物流的最低成本价，筛选最低价格
     * @return
     */
    FaCostFreight selectFaCompanyCostFreight(FaCourierOrderEntity entity);

    /**
     * 取消订单
     * @param id
     */
    String cancelCourierOrder(String stateType,String content,Integer id);

    /**
     * 地址解析
     * @param text
     * @return
     */
    Object addressAnalysis(String text);

    /**
     * 物流信息
     * @param id
     * @return
     */
    Object queryCourierTrack(Integer id);

    void saveOrderExt(Integer id,String cname,String cvalue);

    void delete(Integer id);

    Map<String, String> getOrderNoCompanyIdUserId();

    Integer shareCreate(FaCourierOrderEntity resources);

    Object batchCreate(List<FaCourierOrderEntity> resources);

//    /**
//     * 查询所有数据不分页
//     * @param criteria 条件参数
//     * @return List<FaCourierOrderDto>
//     */
//    List<FaCourierOrderDto> queryAll(FaCourierOrderQueryCriteria criteria);
//
//    /**
//     * 根据ID查询
//     * @param id ID
//     * @return FaCourierOrderDto
//     */
//    FaCourierOrderDto findById(Integer id);
//    /**
//     * 编辑
//     * @param resources /
//     */
//    void update(FaCourierOrder resources);
//
//    /**
//     * 多选删除
//     * @param ids /
//     */
//    void deleteAll(Integer[] ids);
//
//    /**
//     * 导出数据
//     * @param all 待导出的数据
//     * @param response /
//     * @throws IOException /
//     */
//    void download(List<FaCourierOrderDto> all, HttpServletResponse response) throws IOException;
}
