package com.cmhq.core.service;

import com.cmhq.core.model.FaCompanyEntity;
import com.cmhq.core.model.param.CompanyQuery;
import io.swagger.models.auth.In;
import me.zhengjie.QueryResult;


public interface FaCompanyService {

    QueryResult<FaCompanyEntity> list(CompanyQuery query);

    Integer edit(FaCompanyEntity entity) throws Exception;

    void delete(Integer id);
}
