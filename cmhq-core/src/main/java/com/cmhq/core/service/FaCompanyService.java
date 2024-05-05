package com.cmhq.core.service;

import com.cmhq.core.model.FaCompanyCostEntity;
import com.cmhq.core.model.FaCompanyEntity;
import com.cmhq.core.model.dto.FaCostFreight;
import com.cmhq.core.model.param.CompanyQuery;
import io.swagger.models.auth.In;
import me.zhengjie.QueryResult;
import me.zhengjie.modules.system.domain.User;

import java.util.List;


public interface FaCompanyService {

    QueryResult<FaCompanyEntity> list(CompanyQuery query);

    Integer edit(FaCompanyEntity entity) ;

    FaCompanyEntity selectById(Integer id);

    List<FaCompanyCostEntity> selectFaCompanyCostList();


    /**
     * 商户创建子账户
     * @param resources
     * @return
     * @throws Exception
     */
    Integer createChildUser(User resources) ;

    void delete(Integer id);
}
