package com.cmhq.core.service;

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
//
//    /**
//     * 创建
//     * @param resources /
//     */
//    void create(FaCourierOrder resources);
//
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
