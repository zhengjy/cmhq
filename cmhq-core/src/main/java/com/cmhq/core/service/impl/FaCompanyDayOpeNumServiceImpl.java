package com.cmhq.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cmhq.core.dao.FaCompanyDayOpeNumDao;
import com.cmhq.core.model.FaCompanyDayOpeNumEntity;
import com.cmhq.core.model.FaCompanyEntity;
import com.cmhq.core.service.FaCompanyDayOpeNumService;
import com.cmhq.core.service.FaCompanyService;
import com.cmhq.core.util.CurrentUserContent;
import io.swagger.models.auth.In;
import me.zhengjie.utils.SecurityUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Jiyang.Zheng on 2024/4/22 15:10.
 */
@Service
public class FaCompanyDayOpeNumServiceImpl implements FaCompanyDayOpeNumService {
    @Autowired
    private FaCompanyDayOpeNumDao faCompanyDayOpeNumDao;
    @Autowired
    private FaCompanyService faCompanyService;

    /**
     * 并发量大会存在幂等问题,后期业务量大可以优化
     * @param startDate
     * @param endDate
     * @param opeType
     * @return
     */
    @Override
    public double selectValue(String startDate, String endDate, String opeType) {
        LambdaQueryWrapper<FaCompanyDayOpeNumEntity> lam = new LambdaQueryWrapper<>();
        lam.ge(FaCompanyDayOpeNumEntity::getOpeDate,startDate);
        lam.le(FaCompanyDayOpeNumEntity::getOpeDate,endDate);
        lam.eq(FaCompanyDayOpeNumEntity::getOpeType,opeType);
        try {
            if (SecurityUtils.isCompanyChildUser()){
                if (CurrentUserContent.getCrrentUser().getChildUser() != null){
                    lam.eq(FaCompanyDayOpeNumEntity::getCompanyId,CurrentUserContent.getCrrentUser().getChildUser().getChildCompanyId());
                }
                return 0;
            }else if (SecurityUtils.isCompanyUser()){
                lam.eq(FaCompanyDayOpeNumEntity::getParentCompanyId,SecurityUtils.getCurrentCompanyId())
                        .or().eq(FaCompanyDayOpeNumEntity::getCompanyId,SecurityUtils.getCurrentCompanyId());
            }
        }catch (Exception e){

        }

        List<FaCompanyDayOpeNumEntity> list = faCompanyDayOpeNumDao.selectList(lam);
        if (CollectionUtils.isNotEmpty(list)){
            Double d =list.get(0).getOpeValue();
            if ( d == null){
                return 0D;
            }else {
                return d;
            }
        }
        return 0D;
    }

    @Override
    public double selectValue(String startDate, String endDate, String opeType, Long companyId) {
        Integer childCompamyId = null;
        LambdaQueryWrapper<FaCompanyDayOpeNumEntity> lam = new LambdaQueryWrapper<>();
        lam.ge(FaCompanyDayOpeNumEntity::getOpeDate,startDate);
        lam.le(FaCompanyDayOpeNumEntity::getOpeDate,endDate);
        lam.eq(FaCompanyDayOpeNumEntity::getOpeType,opeType);
        FaCompanyEntity faCompanyEntity = faCompanyService.selectById(companyId.intValue());
        Integer parentId ;
        //子账户
        if (faCompanyEntity.getFid() != null && faCompanyEntity.getFid() > 0){
            childCompamyId = faCompanyEntity.getId();
            parentId = faCompanyEntity.getFid();
        }else {
            parentId = companyId.intValue();
        }
        if (childCompamyId != null){
            lam.eq(FaCompanyDayOpeNumEntity::getCompanyId,childCompamyId);
        }else {
            lam.eq(FaCompanyDayOpeNumEntity::getParentCompanyId,parentId)
                    .or().eq(FaCompanyDayOpeNumEntity::getCompanyId,parentId);
        }
        List<FaCompanyDayOpeNumEntity> list = faCompanyDayOpeNumDao.selectList(lam);
        if (CollectionUtils.isNotEmpty(list)){
            Double d =list.get(0).getOpeValue();
            if ( d == null){
                return 0D;
            }else {
                return d;
            }
        }
        return 0D;
    }

    @Override
    public void updateValue(String date, String opeType, double value) {
        Integer companyId = SecurityUtils.getCurrentCompanyId();
        if (CurrentUserContent.getCrrentUser().getChildUser() != null){
            companyId = CurrentUserContent.getCrrentUser().getChildUser().getChildCompanyId();
        }
        int num = faCompanyDayOpeNumDao.updateValue( companyId,value, opeType,date);
        if (num <= 0){
            FaCompanyDayOpeNumEntity e =  new FaCompanyDayOpeNumEntity();
            if (SecurityUtils.isCompanyChildUser()){
                e.setCompanyId(SecurityUtils.getCurrentCompanyId());
                e.setParentCompanyId(CurrentUserContent.getCrrentUser().getChildUser().getChildCompanyId());
            }else {
                e.setCompanyId(SecurityUtils.getCurrentCompanyId());
            }
            e.setOpeType(opeType);
            e.setOpeDate(date);
            e.setOpeValue(value);
            faCompanyDayOpeNumDao.insert(e);
        }

    }

    @Override
    public void updateValue(String date, String opeType, double value, Integer companyId) {
        FaCompanyEntity faCompanyEntity = faCompanyService.selectById(companyId.intValue());
        Integer childCompamyId = null;
        Integer parentId ;
        //子账户
        if (faCompanyEntity.getFid() != null && faCompanyEntity.getFid() > 0){
            childCompamyId = faCompanyEntity.getId();
            parentId = faCompanyEntity.getFid();
        }else {
            parentId = companyId.intValue();
        }
        int num = faCompanyDayOpeNumDao.updateValue( companyId,value, opeType,date);
        if (num <= 0){
            FaCompanyDayOpeNumEntity e =  new FaCompanyDayOpeNumEntity();
            if (childCompamyId != null){
                e.setCompanyId(parentId);
                e.setParentCompanyId(childCompamyId);
            }else {
                e.setCompanyId(parentId);
            }
            e.setOpeType(opeType);
            e.setOpeDate(date);
            e.setOpeValue(value);
            faCompanyDayOpeNumDao.insert(e);
        }
    }
}
