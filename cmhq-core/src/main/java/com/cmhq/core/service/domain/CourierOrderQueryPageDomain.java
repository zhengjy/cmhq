package com.cmhq.core.service.domain;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cmhq.core.dao.FcCourierOrderDao;
import com.cmhq.core.model.FaCompanyEntity;
import com.cmhq.core.model.FaCourierOrderEntity;
import com.cmhq.core.model.param.CourierOrderQuery;
import com.cmhq.core.util.ContextHolder;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import me.zhengjie.QueryResult;
import me.zhengjie.modules.system.service.UserService;
import me.zhengjie.modules.system.service.dto.RoleSmallDto;
import me.zhengjie.modules.system.service.dto.UserDto;
import me.zhengjie.utils.SecurityUtils;
import net.dreamlu.mica.core.spring.SpringContextUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Created by Jiyang.Zheng on 2024/4/7 9:29.
 */
public class CourierOrderQueryPageDomain {
    CourierOrderQuery query;

    public CourierOrderQueryPageDomain(CourierOrderQuery query){
        this.query = query;

    }
    public QueryResult<FaCourierOrderEntity> handle(){
        FcCourierOrderDao fcCourierOrderDao = SpringContextUtil.getBean(FcCourierOrderDao.class);
        LambdaQueryWrapper<FaCourierOrderEntity> lam = new LambdaQueryWrapper<>();
        serQuery(lam);
        PageHelper.startPage(query.getPageNo(), query.getPageSize());
        List<FaCourierOrderEntity> list = fcCourierOrderDao.selectList(lam);
        PageInfo<FaCourierOrderEntity> page = new PageInfo<>(list);
        QueryResult<FaCourierOrderEntity> queryResult = new QueryResult<>();
        queryResult.setItems(list);
        queryResult.setTotal(page.getTotal());
        return queryResult;
    }

    private void  serQuery(LambdaQueryWrapper<FaCourierOrderEntity> lam){
        UserService userService = SpringContextUtil.getBean(UserService.class);
        //如果是商户系统，权限处理,商户端区分商户本人账号还是普通账号
        if (ContextHolder.isPlatformCompany()){
            //获取当前用户是运维账户,还是商户管理员
            UserDto userDto = userService.findById(SecurityUtils.getCurrentUserId());
            if (CollectionUtils.isNotEmpty(userDto.getRoles())){
                Set<RoleSmallDto> roles=  userDto.getRoles();
                //2 商户,3:商户运维人员
                Optional<RoleSmallDto> role = roles.stream().filter(v -> v.getLevel().equals(2) || v.getLevel().equals(3)).findFirst();;
                if (role.isPresent()){
                    Integer level = role.get().getLevel();
                    lam.eq(FaCourierOrderEntity::getFaCompanyId,SecurityUtils.getCurrentCompanyId());
                    if (level.equals(3)){
                        lam.eq(FaCourierOrderEntity::getCreateUserId,SecurityUtils.getCurrentUserId());
                    }
                }
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

        if (CollectionUtils.isNotEmpty(query.getOrderState())){
            lam.eq(FaCourierOrderEntity::getOrderState,query.getOrderState());
        }

        if (CollectionUtils.isNotEmpty(query.getCanncelOrderState())){
            lam.eq(FaCourierOrderEntity::getCanncelOrderState,query.getCanncelOrderState());
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
        if (StringUtils.isNotEmpty(query.getUserName())){
            lam.like(FaCourierOrderEntity::getCreateUser,query.getUserName());
        }
        lam.orderByDesc(FaCourierOrderEntity::getCreateTime);






    }
}
