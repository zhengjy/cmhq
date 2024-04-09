package com.cmhq.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cmhq.core.dao.FaUserDao;
import com.cmhq.core.model.FaUserEntity;
import com.cmhq.core.model.param.FcUserQuery;
import com.cmhq.core.service.FaUserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import me.zhengjie.QueryResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Jiyang.Zheng on 2024/4/9 13:17.
 */
@Service
public class FaUserServiceImpl  implements FaUserService {
    @Autowired
    private FaUserDao faUserDao;

    @Override
    public QueryResult<FaUserEntity> list(FcUserQuery query) {
        PageHelper.startPage(query.getPageNo(), query.getPageSize()).setOrderBy("createtime desc");
        LambdaQueryWrapper<FaUserEntity> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotEmpty(query.getUsername())){
            queryWrapper.like(FaUserEntity::getUsername,query.getUsername());
        }
        if (StringUtils.isNotEmpty(query.getNickname())){
            queryWrapper.like(FaUserEntity::getNickname,query.getNickname());
        }
        if (StringUtils.isNotEmpty(query.getSTime())){
            queryWrapper.ge(FaUserEntity::getCreatetime, query.getSTime())
                    .le(FaUserEntity::getCreatetime,query.getETime());
        }
        if (StringUtils.isNotEmpty(query.getStatus())){
            queryWrapper.eq(FaUserEntity::getStatus,query.getStatus());
        }
        List<FaUserEntity> list = faUserDao.selectList(queryWrapper);
        PageInfo<FaUserEntity> page = new PageInfo<>(list);
        QueryResult<FaUserEntity> queryResult = new QueryResult<>();
        queryResult.setItems(list);
        queryResult.setTotal(page.getTotal());
        return queryResult;
    }
}
