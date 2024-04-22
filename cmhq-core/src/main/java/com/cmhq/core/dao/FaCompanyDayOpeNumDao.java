package com.cmhq.core.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cmhq.core.model.FaCompanyDayOpeNumEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * Created by Jiyang.Zheng on 2024/4/22 13:28.
 */
@Mapper
public interface FaCompanyDayOpeNumDao extends BaseMapper<FaCompanyDayOpeNumEntity> {
    int updateValue(@Param("companyId") Integer companyId, @Param("opeValue") Double opeValue,  @Param("opeType") String opeType,  @Param("opeDate") String opeDate);
}
