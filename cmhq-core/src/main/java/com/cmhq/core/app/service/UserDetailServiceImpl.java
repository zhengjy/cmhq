package com.cmhq.core.app.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cmhq.core.app.dao.FaSaleUserDao;
import com.cmhq.core.app.model.FaUserEntity;
import me.zhengjie.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component("fauserDetailServiceImpl2")
public class UserDetailServiceImpl implements UserDetailsService,UserService {

    @Autowired
    private FaSaleUserDao userDAO;



    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        FaUserEntity sysUser = userDAO.selectOne(new LambdaQueryWrapper<FaUserEntity>().eq(FaUserEntity::getUsername,s));
        if (sysUser == null){
            throw new UsernameNotFoundException("用户不存在");
        }
        return new User(sysUser.getUsername(),sysUser.getPassword(),new ArrayList<>());
    }

    @Override
    public FaUserEntity queryCurrentUser() {
        FaUserEntity sysUser = userDAO.selectOne(new LambdaQueryWrapper<FaUserEntity>().eq(FaUserEntity::getUsername, SecurityUtils.getCurrentUsername()));
        if (sysUser == null){
            throw new UsernameNotFoundException("用户不存在");
        }
        return sysUser;
    }
}