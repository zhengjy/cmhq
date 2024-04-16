package com.cmhq.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cmhq.core.dao.FaCompanyDao;
import com.cmhq.core.model.FaCompanyEntity;
import com.cmhq.core.model.param.CompanyQuery;
import com.cmhq.core.service.FaCompanyService;
import com.cmhq.core.util.CurrentUserContent;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.QueryResult;
import me.zhengjie.modules.system.domain.Dept;
import me.zhengjie.modules.system.domain.Job;
import me.zhengjie.modules.system.domain.Role;
import me.zhengjie.modules.system.domain.User;
import me.zhengjie.modules.system.service.DeptService;
import me.zhengjie.modules.system.service.UserService;
import me.zhengjie.modules.system.service.dto.UserDto;
import me.zhengjie.utils.SecurityUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 *  Created by Jiyang.Zheng on 2024/4/5 9:31.
 */
@Slf4j
@Service
public class FaCompanyServiceImpl implements FaCompanyService {
    @Autowired
    private FaCompanyDao faCompanyDao;
    @Resource
    private UserService userService;
    @Resource
    private DeptService deptService;
    @Resource
    private  PasswordEncoder passwordEncoder;

    @Override
    public QueryResult<FaCompanyEntity> list(CompanyQuery query) {
        PageHelper.startPage(query.getPageNo(), query.getPageSize()).setOrderBy("create_time desc");
        LambdaQueryWrapper<FaCompanyEntity> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotEmpty(query.getMobile())){
            queryWrapper.like(FaCompanyEntity::getName,query.getCompanyName());
        }
        if (StringUtils.isNotEmpty(query.getSTime())){
            queryWrapper.ge(FaCompanyEntity::getCreateTime, query.getSTime())
                    .le(FaCompanyEntity::getCreateTime,query.getETime());
        }
        if (StringUtils.isNotEmpty(query.getCompanyName())){
            queryWrapper.like(FaCompanyEntity::getName,query.getCompanyName());
        }

        List<FaCompanyEntity>  list = faCompanyDao.selectList(queryWrapper);
        PageInfo<FaCompanyEntity> page = new PageInfo<>(list);
        QueryResult<FaCompanyEntity> queryResult = new QueryResult<>();
        queryResult.setItems(list);
        queryResult.setTotal(page.getTotal());
        return queryResult;
    }

    @Override
    public Integer edit(FaCompanyEntity entity) throws Exception {
        if (entity.getId() == null){
            String pwd = passwordEncoder.encode(entity.getPassword());
            entity.setPassword(pwd);
            User user = new User();
            user.setUsername(entity.getMobile());

            Dept dept = new Dept();
            dept.setName(entity.getCompanyName());
            dept.setDeptSort(999);
            dept.setEnabled(true);
            dept.setSubCount(0);
            deptService.create(dept);
            user.setDept(dept);

            Set<Role> roles = new HashSet<>();
            Role role = new Role();
            role.setId(-1L);
            roles.add(role);
            user.setRoles(roles);

            Set<Job> jobs = new HashSet<>();
            Job job = new Job();
            job.setId(12L);
            jobs.add(job);
            user.setJobs(jobs);

            user.setNickName(entity.getCompanyName());
            user.setGender("男");
            user.setPhone(entity.getMobile());
            user.setEmail(System.currentTimeMillis()+"@qq.com");
            user.setAvatarName("avatar.jpg");
            user.setAvatarPath("/usr/app/file/");
            user.setPassword(pwd);

            user.setIsAdmin(false);
            user.setPlatform("company");
            user.setEnabled(true);

            user.setCreateBy("admin");
            user.setUpdateBy("admim");
            //保存商户表
            faCompanyDao.insert(entity);
            user.setCompanyId(entity.getId());
            //保存用户表、商户用户默认权限
            userService.create(user);
        }else {
            faCompanyDao.updateById(entity);
        }
        return entity.getId();
    }

    @Transactional
    @Override
    public Integer createChildUser(User resources)  {
        // 默认密码 123456
        resources.setPassword(passwordEncoder.encode("123456"));
        resources.setPlatform("company");
        resources.setAvatarName("avatar.jpg");
        resources.setAvatarPath("/usr/app/file/");
        //保存商户表
        FaCompanyEntity currentCompany = CurrentUserContent.getCurrentCompany();
        FaCompanyEntity entity = new FaCompanyEntity();
        Integer fcid = entity.getId();
        entity.setCompanyName(currentCompany.getCompanyName());
        entity.setPassword(resources.getPassword());
        entity.setMobile(resources.getPhone());
        entity.setFid(fcid);
        entity.setStatus(currentCompany.getStatus());
        entity.setAvatar(currentCompany.getAvatar());
        if (resources.getChildUser() != null){
            entity.setZiMaxNum(resources.getChildUser().getZiMaxNum());
            entity.setZiMaxMoney(resources.getChildUser().getZiMaxMoney());
            entity.setZiOneMaxMoney(resources.getChildUser().getZiOneMaxMoney());

        }
        entity.setName(resources.getNickName());
        entity.setHobbydata(currentCompany.getHobbydata());
        faCompanyDao.insert(entity);
        if (resources.getChildUser() != null){
            resources.getChildUser().setChildCompanyId(entity.getId());
        }
        resources.setCompanyId(SecurityUtils.getCurrentCompanyId());
        userService.create(resources);
        return resources.getId().intValue();
    }

    @Transactional
    @Override
    public void delete(Integer id) {
        FaCompanyEntity entity = faCompanyDao.selectById(id);
        faCompanyDao.deleteById(id);
        try {
            UserDto dto = userService.findByName(entity.getCompanyName());
            if (dto != null){
                Set set = new HashSet<>();
                set.add(dto.getId());
                userService.delete(set);
            }
        }catch (Exception e){
            log.error("",e);
        }

    }
}
