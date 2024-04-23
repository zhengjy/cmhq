package com.cmhq.core.service;

import com.cmhq.core.model.FaCompanyEntity;
import com.cmhq.core.model.param.CompanyQuery;
import io.swagger.models.auth.In;
import me.zhengjie.QueryResult;
import me.zhengjie.modules.system.domain.User;


public interface FaCompanyService {

    QueryResult<FaCompanyEntity> list(CompanyQuery query);

    Integer edit(FaCompanyEntity entity) ;

    FaCompanyEntity selectById(Integer id);

    /**
     * 商户创建子账户
     * @param resources
     * @return
     * @throws Exception
     */
    Integer createChildUser(User resources) ;

    void delete(Integer id);
}
