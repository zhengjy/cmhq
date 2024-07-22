package me.zhengjie.modules.security.config;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class SysUserAuthenticationToken extends UsernamePasswordAuthenticationToken {
    /**
     * 该方法只是用来选择对应的provider处理器
     */
    public SysUserAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials,authorities);
    }
    public SysUserAuthenticationToken(Object principal, Object credentials) {
        super(principal, credentials);
    }
}
