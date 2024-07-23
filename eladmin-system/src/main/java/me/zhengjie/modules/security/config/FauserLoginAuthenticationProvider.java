package me.zhengjie.modules.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

@Component
public class FauserLoginAuthenticationProvider implements AuthenticationProvider {

    @Resource(name = "fauserDetailServiceImpl")
    private UserDetailsService userDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // 获取表单用户名
        String username = (String) authentication.getPrincipal();
        // 获取表单用户填写的密码
        String password = (String) authentication.getCredentials();

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        String password1 = userDetails.getPassword();
        if (!Objects.equals(password,password1)){
            throw new BadCredentialsException("用户名或密码不正确");
        }

        return new FaUserAuthenticationToken(userDetails,password,userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.equals(FaUserAuthenticationToken.class);
    }
}
