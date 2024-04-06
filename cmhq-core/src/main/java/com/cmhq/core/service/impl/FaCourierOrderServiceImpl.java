package com.cmhq.core.service.impl;

import com.cmhq.core.dao.FcCourierOrderDao;
import com.cmhq.core.model.FaCourierOrderEntity;
import com.cmhq.core.model.param.CourierOrderQuery;
import com.cmhq.core.service.FaCourierOrderService;
import me.zhengjie.QueryResult;
import me.zhengjie.utils.SecurityUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Jiyang.Zheng on 2024/4/6 14:00.
 */
@Service
public class FaCourierOrderServiceImpl implements FaCourierOrderService {

    @Autowired
    private FcCourierOrderDao fcCourierOrderDao;

    @Override
    public QueryResult<FaCourierOrderEntity> queryAll(CourierOrderQuery query) {
        //查询带商户权限控制，为空则不校验商户信息
        if (SecurityUtils.isPlatformCompany()){
//            if (){
//
//            }

        }

        //用户权限
        return null;
    }

//    @Override
//    public List<FaCourierOrderDto> queryAll(FaCourierOrderQueryCriteria criteria) {
//        return faCourierOrderMapper.toDto(faCourierOrderRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder)));
//    }
//
//    @Override
//    @Transactional
//    public FaCourierOrderDto findById(Integer id) {
//        FaCourierOrder faCourierOrder = faCourierOrderRepository.findById(id).orElseGet(FaCourierOrder::new);
//        ValidationUtil.isNull(faCourierOrder.getId(), "FaCourierOrder", "id", id);
//        return faCourierOrderMapper.toDto(faCourierOrder);
//    }
//
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public void create(FaCourierOrder resources) {
//        faCourierOrderRepository.save(resources);
//    }
//
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public void update(FaCourierOrder resources) {
//        FaCourierOrder faCourierOrder = faCourierOrderRepository.findById(resources.getId()).orElseGet(FaCourierOrder::new);
//        ValidationUtil.isNull(faCourierOrder.getId(), "FaCourierOrder", "id", resources.getId());
//        faCourierOrder.copy(resources);
//        faCourierOrderRepository.save(faCourierOrder);
//    }
//
//    @Override
//    public void deleteAll(Integer[] ids) {
//        for (Integer id : ids) {
//            faCourierOrderRepository.deleteById(id);
//        }
//    }
//
//    @Override
//    public void download(List<FaCourierOrderDto> all, HttpServletResponse response) throws IOException {
//        List<Map<String, Object>> list = new ArrayList<>();
//        for (FaCourierOrderDto faCourierOrder : all) {
//            Map<String, Object> map = new LinkedHashMap<>();
//            map.put("商户id", faCourierOrder.getFaCompanyId());
//            map.put("订单号", faCourierOrder.getOrderNo());
//            map.put("快递公司订单号", faCourierOrder.getCourierOrderNo());
//            map.put("快递公司运单号", faCourierOrder.getCourierCompanyWaybillNo());
//            map.put("物品分类", faCourierOrder.getGoodsType());
//            map.put("物品名称", faCourierOrder.getGoodsName());
//            map.put("重量", faCourierOrder.getWeight());
//            map.put("宽度", faCourierOrder.getWidth());
//            map.put("长度", faCourierOrder.getLength());
//            map.put("高", faCourierOrder.getHeight());
//            map.put("商品金额", faCourierOrder.getEstimatePrice());
//            map.put("发货人", faCourierOrder.getFromName());
//            map.put("发货手机号", faCourierOrder.getFromMobile());
//            map.put("发出省份", faCourierOrder.getFromProv());
//            map.put("发出城市", faCourierOrder.getFromCity());
//            map.put("发出区县", faCourierOrder.getFromArea());
//            map.put("发出地址", faCourierOrder.getFromAddress());
//            map.put("收货人", faCourierOrder.getToName());
//            map.put("收货手机号", faCourierOrder.getToMobile());
//            map.put("收货省份", faCourierOrder.getToProv());
//            map.put("收货城市", faCourierOrder.getToCity());
//            map.put("收货区县", faCourierOrder.getToArea());
//            map.put("收货地址", faCourierOrder.getToAddress());
//            map.put("0未结算1已结算", faCourierOrder.getIsJiesuan());
//            map.put("1运输中2派件中3已签收", faCourierOrder.getOrderState());
//            map.put("创建人", faCourierOrder.getCreateUser());
//            map.put(" createTime", faCourierOrder.getCreateTime());
//            map.put("更新人", faCourierOrder.getUpdateUser());
//            map.put(" updateTime", faCourierOrder.getUpdateTime());
//            list.add(map);
//        }
//        FileUtil.downloadExcel(list, response);
//    }
}
