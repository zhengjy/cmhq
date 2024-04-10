package com.cmhq.core.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cmhq.core.model.FaCompanyEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface FaCompanyDao  extends BaseMapper<FaCompanyEntity> {

    int updateMoney(@Param("companyId") Integer companyId, @Param("money") Double money);
}
