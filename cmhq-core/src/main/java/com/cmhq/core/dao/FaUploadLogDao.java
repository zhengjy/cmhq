package com.cmhq.core.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cmhq.core.model.UploadLogEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * Created by Jiyang.Zheng on 2024/4/7 13:18.
 */
@Mapper
public interface FaUploadLogDao extends BaseMapper<UploadLogEntity> {
}
