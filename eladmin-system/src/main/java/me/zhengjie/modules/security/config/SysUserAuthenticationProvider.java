package me.zhengjie.modules.security.config;

import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

public class SysUserAuthenticationProvider extends DaoAuthenticationProvider {
    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.equals(SysUserAuthenticationToken.class);
    }
}
