package com.cmhq.core.service.domain;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cmhq.core.dao.FaCourierOrderDao;
import com.cmhq.core.dao.FaCourierOrderExtDao;
import com.cmhq.core.model.FaCompanyEntity;
import com.cmhq.core.model.FaCourierOrderEntity;
import com.cmhq.core.model.FaCourierOrderExtEntity;
import com.cmhq.core.model.param.CourierOrderQuery;
import com.cmhq.core.service.FaCompanyService;
import com.cmhq.core.util.SpringApplicationUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import me.zhengjie.QueryResult;
import me.zhengjie.modules.system.service.UserService;
import me.zhengjie.modules.system.service.dto.UserDto;
import me.zhengjie.utils.SecurityUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Jiyang.Zheng on 2024/4/7 9:29.
 */
public class CourierOrderQueryPageDomain {
    CourierOrderQuery query;

    public CourierOrderQueryPageDomain(CourierOrderQuery query){
        this.query = query;

    }
    public QueryResult<FaCourierOrderEntity> handle(){
        PageHelper.startPage(query.getPageNo(), query.getPageSize());
        FaCourierOrderDao faCourierOrderDao = SpringApplicationUtils.getBean(FaCourierOrderDao.class);
        FaCourierOrderExtDao faCourierOrderExtDao = SpringApplicationUtils.getBean(FaCourierOrderExtDao.class);
        FaCompanyService faCompanyService = SpringApplicationUtils.getBean(FaCompanyService.class);
        UserService userService = SpringApplicationUtils.getBean(UserService.class);
        LambdaQueryWrapper<FaCourierOrderEntity> lam = new LambdaQueryWrapper<>();
        serQuery(lam);
        List<FaCourierOrderEntity> list = faCourierOrderDao.selectList(lam);
        PageInfo<FaCourierOrderEntity> page = new PageInfo<>(list);
        QueryResult<FaCourierOrderEntity> queryResult = new QueryResult<>();
        if (list != null){
            list.stream().forEach(v ->{
                FaCompanyEntity faCompanyEntity = faCompanyService.selectById(v.getFaCompanyId());
                if (faCompanyEntity != null){
                    v.setCompanyName(faCompanyEntity.getCompanyName());
                }
                if (faCompanyEntity != null && faCompanyEntity.getFid() != null && v.getCreateUserId() != null){
                    UserDto dto = userService.findById(v.getCreateUserId());
                    if (dto != null){
                        v.setCreateUserName(dto.getNickName());
                    }
                }
                List<FaCourierOrderExtEntity> extEntityList = faCourierOrderExtDao.selectList(new LambdaQueryWrapper<FaCourierOrderExtEntity>().eq(FaCourierOrderExtEntity::getCourierOrderId,v.getId()));
                if (extEntityList != null){
                    v.setCourierOrderExtend(JSONObject.toJSONString(extEntityList.stream().collect(Collectors.toMap(FaCourierOrderExtEntity::getCname,FaCourierOrderExtEntity::getCvalue))));
                }
            });
        }
        queryResult.setItems(list);
        queryResult.setTotal(page.getTotal());
        return queryResult;
    }

    public void  serQuery(LambdaQueryWrapper<FaCourierOrderEntity> lam){
        //如果是商户系统，权限处理,商户端区分商户本人账号还是普通账号
        //获取当前用户是运维账户,还是商户管理员
        if (StringUtils.isEmpty(query.getOrderNo() ) && query.getId() == null){
            if (SecurityUtils.isPlatformCompany()){

                lam.eq(FaCourierOrderEntity::getFaCompanyId,SecurityUtils.getCurrentCompanyId());
//                if (SecurityUtils.isCompanyChildUser()){
//                    lam.eq(FaCourierOrderEntity::getFaCompanyId,SecurityUtils.getCurrentCompanyId());
//                    lam.eq(FaCourierOrderEntity::getCreateUserId,SecurityUtils.getCurrentUserId());
//                }else if (SecurityUtils.isCompanyUser()){
//                    lam.eq(FaCourierOrderEntity::getFaCompanyId,SecurityUtils.getCurrentCompanyId());
//                }
            }
            if (SecurityUtils.isPlatformCompany()){
                if (StringUtils.isNotEmpty(query.getUserName())){
                    lam.inSql(FaCourierOrderEntity::getCreateUserId,"select user_id from sys_user where username  like '%"+query.getUserName()+"%' and company_Id = " + SecurityUtils.getCurrentCompanyId() );
                }
            }
        }

        lam.eq(FaCourierOrderEntity::getXdelkid,0);


        if (StringUtils.isNotEmpty(query.getCourierCompanyCode())){
            lam.eq(FaCourierOrderEntity::getCourierCompanyCode,query.getCourierCompanyCode());
        }
        if (StringUtils.isNotEmpty(query.getPagetype()) ){
            //页面菜单：1：商户-订单列表，2：商户-订单取消记录，3：商户-取消订单，4：总后台-发货订单，5：总后台-订单取消记录，6：总后台-取消订单审核，7：总后台-台账管理
            if (StringUtils.equals(query.getPagetype(),"1") || StringUtils.equals(query.getPagetype(),"4")){
                lam.eq(FaCourierOrderEntity::getCancelType,0);
            }else if (StringUtils.equals(query.getPagetype(),"2") || StringUtils.equals(query.getPagetype(),"5")){
                lam.notIn(FaCourierOrderEntity::getCancelType,0);
            }else if (StringUtils.equals(query.getPagetype(),"3") ){
                lam.in(FaCourierOrderEntity::getOrderState,0,1);
            }else if (StringUtils.equals(query.getPagetype(),"6")){
                lam.in(FaCourierOrderEntity::getCancelOrderState,-1);
            }else {
                lam.eq(FaCourierOrderEntity::getCancelType,0);
            }
        }
        if (StringUtils.isNotEmpty(query.getOrderNo())){
            lam.eq(FaCourierOrderEntity::getOrderNo,query.getOrderNo());
        }
        if (query.getId() != null){
            lam.eq(FaCourierOrderEntity::getId,query.getId());
        }
        if (StringUtils.isNotEmpty(query.getCourierCompanyWaybillNo())){
            lam.eq(FaCourierOrderEntity::getCourierCompanyWaybillNo,query.getCourierCompanyWaybillNo());
        }
        if (StringUtils.isNotEmpty(query.getGoodsName())){
            lam.eq(FaCourierOrderEntity::getGoodsName,query.getGoodsName());
        }
        if (StringUtils.isNotEmpty(query.getIsCancel())){
            lam.isNotNull(FaCourierOrderEntity::getCancelType);
        }

        if (CollectionUtils.isNotEmpty(query.getOrderState())){
            lam.in(FaCourierOrderEntity::getOrderState,query.getOrderState());
        }

        if (CollectionUtils.isNotEmpty(query.getWuliuState())){
            lam.in(FaCourierOrderEntity::getWuliuState,query.getWuliuState());
        }

        if (CollectionUtils.isNotEmpty(query.getCancelOrderState())){
            lam.in(FaCourierOrderEntity::getCancelOrderState,query.getCancelOrderState());
        }

        if (StringUtils.isNotEmpty(query.getOrderIsError())){
            lam.eq(FaCourierOrderEntity::getOrderIsError,query.getOrderIsError());
        }

        if (StringUtils.isNotEmpty(query.getIsJiesuan())){
            lam.eq(FaCourierOrderEntity::getIsJiesuan,query.getIsJiesuan());
        }

        if (StringUtils.isNotEmpty(query.getFromName())){
            lam.eq(FaCourierOrderEntity::getFromName,query.getFromName());
        }

        if (StringUtils.isNotEmpty(query.getFromMobile())){
            lam.eq(FaCourierOrderEntity::getFromMobile,query.getFromMobile());
        }

        if (StringUtils.isNotEmpty(query.getToName())){
            lam.eq(FaCourierOrderEntity::getToName,query.getToName());
        }

        if (StringUtils.isNotEmpty(query.getToMobile())){
            lam.eq(FaCourierOrderEntity::getToMobile,query.getToMobile());
        }

        if (StringUtils.isNotEmpty(query.getSTime())){
            lam.ge(FaCourierOrderEntity::getCreateTime, query.getSTime())
                    .le(FaCourierOrderEntity::getCreateTime,query.getETime());
        }

        if (StringUtils.isNotEmpty(query.getCompanyName())){
            lam.inSql(FaCourierOrderEntity::getFaCompanyId,"select id from fa_company where company_name like '%"+query.getCompanyName()+"%'");
        }
        lam.orderByDesc(FaCourierOrderEntity::getCreateTime);
        //TODO 扩展字段查询

    }
}
