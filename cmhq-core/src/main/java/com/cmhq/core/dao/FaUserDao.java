package com.cmhq.core.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cmhq.core.model.FaUserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * Created by Jiyang.Zheng on 2024/4/9 13:16.
 */
@Mapper
public interface FaUserDao extends BaseMapper<FaUserEntity> {
    int addMoney(@Param("id") Integer id, @Param("money") Double money);
    int minusMoney(@Param("id") Integer id, @Param("money") Double money);
}
