package com.cmhq.core.app.dao;

import com.cmhq.core.app.model.param.HomeOrderQuery;
import com.cmhq.core.app.model.param.HomeQuery;
import com.cmhq.core.app.model.rsp.HomeCompanyRsp;
import com.cmhq.core.app.model.rsp.HomeOrderRsp;
import me.zhengjie.base.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface HomeDao extends BaseMapper {
    List<HomeCompanyRsp> selectCompanyList(HomeQuery query);
    List<HomeOrderRsp> selectOrderList(HomeOrderQuery query);

}
