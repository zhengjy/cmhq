package com.cmhq.core.service;

import com.cmhq.core.model.FaUserEntity;
import com.cmhq.core.model.param.FcUserQuery;
import me.zhengjie.QueryResult;

/**
 * Created by Jiyang.Zheng on 2024/4/9 13:17.
 */
public interface FaUserService {

    QueryResult<FaUserEntity> list(FcUserQuery query);
}
