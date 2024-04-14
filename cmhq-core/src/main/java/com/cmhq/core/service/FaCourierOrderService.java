package com.cmhq.core.service;

import com.cmhq.core.api.dto.response.CourierFreightChargeDto;
import com.cmhq.core.model.FaCourierOrderEntity;
import com.cmhq.core.model.param.CourierOrderQuery;
import me.zhengjie.QueryResult;

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
    Object getCourierFreightCharge(FaCourierOrderEntity entity);

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
