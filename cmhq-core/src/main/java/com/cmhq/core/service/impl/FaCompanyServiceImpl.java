package com.cmhq.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cmhq.core.dao.FaCompanyCostDao;
import com.cmhq.core.dao.FaCompanyDao;
import com.cmhq.core.dao.FaUserDao;
import com.cmhq.core.model.FaCompanyCostEntity;
import com.cmhq.core.model.FaCompanyEntity;
import com.cmhq.core.model.FaUserEntity;
import com.cmhq.core.model.dto.FaCostFreight;
import com.cmhq.core.model.param.CompanyQuery;
import com.cmhq.core.service.FaCompanyService;
import com.cmhq.core.util.CurrentUserContent;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.QueryResult;
import me.zhengjie.config.RsaProperties;
import me.zhengjie.modules.system.domain.Dept;
import me.zhengjie.modules.system.domain.Job;
import me.zhengjie.modules.system.domain.Role;
import me.zhengjie.modules.system.domain.User;
import me.zhengjie.modules.system.service.DeptService;
import me.zhengjie.modules.system.service.UserService;
import me.zhengjie.modules.system.service.dto.UserDto;
import me.zhengjie.utils.RsaUtils;
import me.zhengjie.utils.SecurityUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 *  Created by Jiyang.Zheng on 2024/4/5 9:31.
 */
@Slf4j
@Service
public class FaCompanyServiceImpl implements FaCompanyService, InitializingBean {
    @Autowired
    private FaCompanyDao faCompanyDao;
    @Resource
    private UserService userService;
    @Resource
    private FaUserDao faUserDao;
    @Resource
    private DeptService deptService;
    @Resource
    private  PasswordEncoder passwordEncoder;
    @Autowired
    private FaCompanyCostDao faCompanyCostDao;

    public static Cache<String, List<FaCompanyCostEntity>> cache = CacheBuilder.newBuilder().expireAfterWrite(12, TimeUnit.HOURS).build();


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
        if (StringUtils.isNotEmpty(query.getNotCid())){
            queryWrapper.eq(FaCompanyEntity::getFid,0);
        }
        if (query.getId() != null){
            queryWrapper.eq(FaCompanyEntity::getId, query.getId());
        }
        queryWrapper.eq(FaCompanyEntity::getIsDelete,"N");
        List<FaCompanyEntity>  list = faCompanyDao.selectList(queryWrapper);
        PageInfo<FaCompanyEntity> page = new PageInfo<>(list);
        if (list != null){
            list.stream().forEach(v ->{
                if (v.getFid() != null && v.getFid() > 0){
                    FaUserEntity faUserEntity = faUserDao.selectById(v.getFUser());
                    if (faUserEntity != null){
                        v.setFUserName(faUserEntity.getUsername());
                    }
                    try {
                        UserDto dto = getUserDto(v);
                        if (dto != null && dto.getChildUser() != null){
                            v.setZiMaxNum(dto.getChildUser().getZiMaxNum());
                            v.setZiMaxMoney(dto.getChildUser().getZiMaxMoney());
                            v.setZiOneMaxMoney(dto.getChildUser().getZiOneMaxMoney());
                            v.setCancelMaxNum(dto.getChildUser().getCancelMaxNum());
                            v.setCancelMaxMoney(dto.getChildUser().getCancelMaxMoney());
                        }
                    }catch (Exception e){
                        log.error("",e.getMessage());
                    }
                }

            });
        }
        QueryResult<FaCompanyEntity> queryResult = new QueryResult<>();
        queryResult.setItems(list);
        queryResult.setTotal(page.getTotal());
        return queryResult;
    }

    @Override
    public Integer edit(FaCompanyEntity entity) {
        if (entity.getId() == null){
            List<FaCompanyEntity> list = faCompanyDao.selectList(new LambdaQueryWrapper<FaCompanyEntity>().eq(FaCompanyEntity::getMobile,entity.getMobile()));
            if (CollectionUtils.isNotEmpty(list)){
                throw new RuntimeException("手机号已经存在！");
            }
            entity.setMoney(0D);
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
            user.setAvatarName("avatar-20240516101826966.png");
            user.setAvatarPath("/home/eladmin/avatar/avatar-20240516101826966.png");
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
            entity.setMobile(null);
            faCompanyDao.updateById(entity);
        }
        return entity.getId();
    }

    @Override
    public FaCompanyEntity selectById(Integer id) {
        CompanyQuery query = new CompanyQuery();
        query.setId(id);
        QueryResult<FaCompanyEntity> queryResult = list(query);
        if (queryResult.getTotal() > 0){
            return queryResult.getItems().get(0);
        }
        return null;
    }

    @Override
    public List<FaCompanyCostEntity> selectFaCompanyCostList() {
        String key = "FaCompanyCosts";
        List<FaCompanyCostEntity> list = cache.getIfPresent(key);
        if (CollectionUtils.isEmpty(list)){
            list = faCompanyCostDao.selectList(new LambdaQueryWrapper<>());
            cache.put(key,list);
        }
        return list;
    }

    @Transactional
    @Override
    public Integer createChildUser(User resources)  {
        UserDto userDto = userService.findById(SecurityUtils.getCurrentUserId());
        Dept dept = new Dept();
        dept.setName(userDto.getDept().getName());
        dept.setId(userDto.getDept().getId());
        resources.setDept(dept);

        if (resources.getJobs() == null || resources.getJobs().isEmpty()){
            Set<Job> jobs = new HashSet<>();
            Job job = new Job();
            job.setId(12L);//运维人员
            jobs.add(job);
            resources.setJobs(jobs);
        }

        dept.setEnabled(true);
        dept.setSubCount(0);
        resources.setDept(dept);

        if (resources.getRoles() == null || resources.getRoles().isEmpty()){
            Set<Role> roles = new HashSet<>();
            Role role = new Role();
            role.setId(5L);//商户运维人员角色
            roles.add(role);
            resources.setRoles(roles);
        }




        resources.setUsername(resources.getPhone());
        resources.setEmail(resources.getPhone()+"@qq.com");
        resources.setGender("男");
        resources.setEnabled(true);

        resources.setPlatform("company");
        resources.setAvatarName("avatar-20240516101826966.png");
        resources.setAvatarPath("/home/eladmin/avatar/avatar-20240516101826966.png");
        if (resources.getId() == null){
            resources.setPassword(passwordEncoder.encode(resources.getPassword()));
            //保存商户表
            FaCompanyEntity currentCompany = CurrentUserContent.getCurrentCompany();
            FaCompanyEntity entity = new FaCompanyEntity();
            Integer fcid = currentCompany.getId();
            entity.setCompanyName(currentCompany.getCompanyName());
            entity.setPassword(resources.getPassword());
            entity.setMobile(resources.getPhone());
            entity.setFid(fcid);
            entity.setStatus(currentCompany.getStatus());
            entity.setAvatar(currentCompany.getAvatar());
//        if (resources.getChildUser() != null){
//            entity.setZiMaxNum(resources.getChildUser().getZiMaxNum());
//            entity.setZiMaxMoney(resources.getChildUser().getZiMaxMoney());
//            entity.setZiOneMaxMoney(resources.getChildUser().getZiOneMaxMoney());
//            entity.setCancelMaxMoney(resources.getChildUser().getCancelMaxMoney());
//            entity.setCancelMaxNum(resources.getChildUser().getCancelMaxNum());
//
//        }
            entity.setName(resources.getUsername());
            entity.setHobbydata(currentCompany.getHobbydata());
            faCompanyDao.insert(entity);
            if (resources.getChildUser() != null){
                resources.getChildUser().setChildCompanyId(entity.getId());
            }
            resources.setCompanyId(SecurityUtils.getCurrentCompanyId());
            userService.create(resources);
            return resources.getId().intValue();
        }else {
            try {
                UserDto userDto1 = userService.findById(resources.getId());
                if(StringUtils.isEmpty(resources.getPassword())){
                    resources.setPassword(userDto1.getPassword());
                }else {
                    resources.setPassword(passwordEncoder.encode(resources.getPassword()));
                }
                userService.update(resources);
            }catch (Exception e){
                log.error("",e);
            }
        }
        return null;


    }

    @Transactional
    @Override
    public void delete(Integer id) {
        FaCompanyEntity entity = selectById(id);
        FaCompanyEntity ee= new FaCompanyEntity();
        ee.setId(id);
        ee.setIsDelete("Y");
        faCompanyDao.deleteById(id);
        try {
            UserDto dto = getUserDto(entity);
            if (dto != null){
                userService.delete(Arrays.asList(dto.getId()).stream().collect(Collectors.toSet()));
            }
        }catch (Exception e){
            log.error("",e);
        }

    }

    private UserDto getUserDto(FaCompanyEntity entity){
        UserDto dto = null;
        try {
            dto = userService.findByName(entity.getMobile());
        }catch (Exception e){
            log.error("",e.getMessage());
        }
        if (dto == null){
            try {
                dto = userService.findByName(entity.getName());
            }catch (Exception e){
                log.error("",e.getMessage());
            }
        }
        return dto;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        new Thread(this::selectFaCompanyCostList).start();
    }
}
