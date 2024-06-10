package com.cmhq.app.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cmhq.app.model.FaUserEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FaUserDao extends BaseMapper<FaUserEntity> {
}
