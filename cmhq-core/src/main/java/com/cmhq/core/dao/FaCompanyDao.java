package com.cmhq.core.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cmhq.core.model.FaCompanyEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface FaCompanyDao  extends BaseMapper<FaCompanyEntity> {

    int minusMoney(@Param("companyId") Integer companyId, @Param("money") Double money);
    int minusEstimatePrice(@Param("companyId") Integer companyId, @Param("money") Double money);
    int restMoney(@Param("companyId") Integer companyId, @Param("money") Double money);
    int addMoney(@Param("companyId") Integer companyId, @Param("money") Double money);
}
