package com.cmhq.core.service.impl;

import com.cmhq.core.api.UploadResult;
import com.cmhq.core.api.UploadTypeEnum;
import com.cmhq.core.api.strategy.StrategyFactory;
import com.cmhq.core.api.strategy.Upload;
import com.cmhq.core.dao.FcCourierOrderDao;
import com.cmhq.core.model.FaCourierOrderEntity;
import com.cmhq.core.model.param.CourierOrderQuery;
import com.cmhq.core.service.FaCourierOrderService;
import com.cmhq.core.service.domain.CourierOrderQueryPageDomain;
import me.zhengjie.QueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by Jiyang.Zheng on 2024/4/6 14:00.
 */
@Service
public class FaCourierOrderServiceImpl implements FaCourierOrderService {

    @Autowired
    private FcCourierOrderDao fcCourierOrderDao;

    @Override
    public QueryResult<FaCourierOrderEntity> queryAll(CourierOrderQuery query) {
        return new CourierOrderQueryPageDomain(query).handle();
    }

    @Transactional
    @Override
    public Integer create(FaCourierOrderEntity entity) {
        entity.setOrderNo(System.currentTimeMillis() + "");
        //上传物流公司
        Upload upload = StrategyFactory.getUpload(Objects.requireNonNull(UploadTypeEnum.getMsgByCode(entity.getCourierCompanyCode(), UploadTypeEnum.TYPE_STO_ORDER_UPLOAD.getCodeNickName())));
        UploadResult uploadResult = upload.execute(entity);
        if (uploadResult.getFlag()){
            fcCourierOrderDao.insert(entity);
        }else {
            throw new RuntimeException("物流公司上传失败:"+uploadResult.getErrorMsg());
        }
        //计费
        return null;
    }

    @Override
    public String getCourierFreightCharge(FaCourierOrderEntity entity) {
        entity.setOrderNo(System.currentTimeMillis()+"");
        Upload upload = StrategyFactory.getUpload(Objects.requireNonNull(UploadTypeEnum.getMsgByCode(entity.getCourierCompanyCode(), UploadTypeEnum.TYPE_STO_QUERY_FREIGHT_CHARGE.getCodeNickName())));
        UploadResult uploadResult = upload.execute(entity);
        if (uploadResult.getFlag()){
            return new ArrayList<>(uploadResult.getJsonMsg().values()).get(0);
        }
        return "";
    }

    @Override
    public String cancelCourierOrder(Integer id) {
        FaCourierOrderEntity entity = fcCourierOrderDao.selectById(id);
        Upload upload = StrategyFactory.getUpload(Objects.requireNonNull(UploadTypeEnum.getMsgByCode(entity.getCourierCompanyCode(), UploadTypeEnum.TYPE_STO_CANCEL_COURIER_ORDER.getCodeNickName())));
        UploadResult uploadResult = upload.execute(entity);
        if (uploadResult.getFlag()){
            return new ArrayList<>(uploadResult.getJsonMsg().values()).get(0);
        }
        return "";
    }

    @Override
    public String addressAnalysis(String text) {
        Upload upload = StrategyFactory.getUpload(UploadTypeEnum.TYPE_BAIDU_ADDRDESS);
        UploadResult uploadResult = upload.execute(text);
        if (uploadResult.getFlag()){
            return new ArrayList<>(uploadResult.getJsonMsg().values()).get(0);
        }
        return "";
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
