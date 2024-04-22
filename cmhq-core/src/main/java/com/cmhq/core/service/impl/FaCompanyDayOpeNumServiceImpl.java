package com.cmhq.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cmhq.core.dao.FaCompanyDayOpeNumDao;
import com.cmhq.core.model.FaCompanyDayOpeNumEntity;
import com.cmhq.core.service.FaCompanyDayOpeNumService;
import com.cmhq.core.util.CurrentUserContent;
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
        if (SecurityUtils.isCompanyChildUser()){
            if (CurrentUserContent.getCrrentUser().getChildUser() != null){
                lam.eq(FaCompanyDayOpeNumEntity::getCompanyId,CurrentUserContent.getCrrentUser().getChildUser().getChildCompanyId());
            }
            return 0;
        }else if (SecurityUtils.isCompanyUser()){
            lam.eq(FaCompanyDayOpeNumEntity::getParentCompanyId,SecurityUtils.getCurrentCompanyId())
                    .or().eq(FaCompanyDayOpeNumEntity::getCompanyId,SecurityUtils.getCurrentCompanyId());
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
}
