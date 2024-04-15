package com.cmhq.core.service.domain;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cmhq.core.dao.FaCourierOrderDao;
import com.cmhq.core.model.FaCourierOrderEntity;
import com.cmhq.core.model.param.CourierOrderQuery;
import com.cmhq.core.util.ContextHolder;
import com.cmhq.core.util.CurrentUserContent;
import com.cmhq.core.util.SpringApplicationUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import me.zhengjie.QueryResult;
import me.zhengjie.modules.system.service.UserService;
import me.zhengjie.utils.SecurityUtils;
import net.dreamlu.mica.core.spring.SpringContextUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

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
        LambdaQueryWrapper<FaCourierOrderEntity> lam = new LambdaQueryWrapper<>();
        serQuery(lam);
        List<FaCourierOrderEntity> list = faCourierOrderDao.selectList(lam);
        PageInfo<FaCourierOrderEntity> page = new PageInfo<>(list);
        QueryResult<FaCourierOrderEntity> queryResult = new QueryResult<>();
        queryResult.setItems(list);
        queryResult.setTotal(page.getTotal());
        return queryResult;
    }

    public void  serQuery(LambdaQueryWrapper<FaCourierOrderEntity> lam){
        //如果是商户系统，权限处理,商户端区分商户本人账号还是普通账号
        //获取当前用户是运维账户,还是商户管理员
        if (SecurityUtils.isPlatformCompany()){
            if (SecurityUtils.isCompanyChildUser()){
                lam.eq(FaCourierOrderEntity::getFaCompanyId,SecurityUtils.getCurrentCompanyId());
                lam.eq(FaCourierOrderEntity::getCreateUserId,SecurityUtils.getCurrentUserId());
            }else if (SecurityUtils.isCompanyUser()){
                lam.eq(FaCourierOrderEntity::getFaCompanyId,SecurityUtils.getCurrentCompanyId());
            }
        }


        if (StringUtils.isNotEmpty(query.getCourierCompanyCode())){
            lam.eq(FaCourierOrderEntity::getCourierCompanyCode,query.getCourierCompanyCode());
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
            lam.eq(FaCourierOrderEntity::getOrderState,query.getOrderState());
        }

        if (CollectionUtils.isNotEmpty(query.getCancelOrderState())){
            lam.eq(FaCourierOrderEntity::getCancelOrderState,query.getCancelOrderState());
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
            lam.inSql(FaCourierOrderEntity::getFaCompanyId,"select id from fa_company like '%"+query.getCompanyName()+"%'");
        }
        if (SecurityUtils.isPlatformCompany()){
            if (StringUtils.isNotEmpty(query.getUserName())){
                lam.inSql(FaCourierOrderEntity::getCreateUserId,"select id from sys_user like '%"+query.getUserName()+"%' and company_Id = " + SecurityUtils.getCurrentCompanyId() );
            }
        }
        lam.orderByDesc(FaCourierOrderEntity::getCreateTime);
        //TODO 扩展字段查询

    }
}
