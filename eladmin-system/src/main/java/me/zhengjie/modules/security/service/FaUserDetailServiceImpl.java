package me.zhengjie.modules.security.service;

import me.zhengjie.modules.security.service.dto.JwtUserDto;
import me.zhengjie.modules.system.domain.FaSaleUser;
import me.zhengjie.modules.system.repository.FaSaleUserRepository;
import me.zhengjie.modules.system.service.dto.DeptSmallDto;
import me.zhengjie.modules.system.service.dto.UserLoginDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Optional;

@Component("fauserDetailServiceImpl")
public class FaUserDetailServiceImpl implements UserDetailsService{

    @Autowired
    private FaSaleUserRepository faSaleUserRepository;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        FaSaleUser faSaleUser = new FaSaleUser();
        faSaleUser.setUsername(s);
        //创建实例
        Example<FaSaleUser> ex = Example.of(faSaleUser);
        Optional<FaSaleUser> faSaleUser1 = faSaleUserRepository.findOne(ex);
        if (!faSaleUser1.isPresent()){
            throw new UsernameNotFoundException("用户不存在");
        }
        UserLoginDto loginDto = new UserLoginDto();
        JwtUserDto jwtUserDto = new JwtUserDto(
                loginDto,
                new ArrayList<>(),
                new ArrayList<>());
        loginDto.setUsername(faSaleUser1.get().getUsername());
        loginDto.setDept(new DeptSmallDto());
        loginDto.setPassword(faSaleUser1.get().getPassword());

        return jwtUserDto;
    }


//    @Override
//    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
//        FaUserEntity sysUser = userDAO.selectOne(new LambdaQueryWrapper<FaUserEntity>().eq(FaUserEntity::getUsername,s));
//        if (sysUser == null){
//            throw new UsernameNotFoundException("用户不存在");
//        }
//        return new User(sysUser.getUsername(),sysUser.getPassword(),new ArrayList<>());
//    }
//
//    @Override
//    public FaUserEntity queryCurrentUser() {
//        FaUserEntity sysUser = userDAO.selectOne(new LambdaQueryWrapper<FaUserEntity>().eq(FaUserEntity::getUsername, SecurityContextHolder.getContext().getAuthentication().getPrincipal()));
//        if (sysUser == null){
//            throw new UsernameNotFoundException("用户不存在");
//        }
//        return sysUser;
//    }
}