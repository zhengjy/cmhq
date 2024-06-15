package com.cmhq.app.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cmhq.app.dao.FaUserDao;
import com.cmhq.app.model.FaUserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component("userDetailServiceImpl")
public class UserDetailServiceImpl implements UserDetailsService,UserService {

    @Autowired
    private FaUserDao userDAO;



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
        FaUserEntity sysUser = userDAO.selectOne(new LambdaQueryWrapper<FaUserEntity>().eq(FaUserEntity::getUsername, SecurityContextHolder.getContext().getAuthentication().getPrincipal()));
        if (sysUser == null){
            throw new UsernameNotFoundException("用户不存在");
        }
        return sysUser;
    }
}