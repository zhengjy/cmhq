package com.cmhq.app.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    @Qualifier(value = "userDetailServiceImpl")
    private UserDetailsService userDetailsService;

    @Autowired
    private AppAuthenticationSuccessHandler successAuthenticationHandler;

    @Autowired
    private AppAuthenticationFailureHandler failureAuthenticationHandler;



    @Autowired
    private AuthenticationProvider authenticationProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
        auth.authenticationProvider(authenticationProvider);
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/index.html","/favicon.ico");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .withObjectPostProcessor(new ObjectPostProcessor<FilterSecurityInterceptor>() {
                    @Override
                    public <O extends FilterSecurityInterceptor> O postProcess(O o) {
                        return o;
                    }
                })
                // 静态资源等等
                .antMatchers(
                        HttpMethod.GET,
                        "/*.html",
                        "/**/*.html",
                        "/**/*.css",
                        "/**/*.js",
                        "/webSocket/**"
                ).permitAll()
                // swagger 文档
                .antMatchers("/swagger-ui.html").permitAll()
                .antMatchers("/doc.html").permitAll()
                .antMatchers("/swagger-resources/**").permitAll()
                .antMatchers("/swagger-ui/**").permitAll()
                .antMatchers("/webjars/**").permitAll()
                .antMatchers("/*/api-docs").permitAll()
                .antMatchers("/v3/api-docs").permitAll()
                // 文件
                .antMatchers("/avatar/**").permitAll()
                .antMatchers("/file/**").permitAll()
                // 阿里巴巴 druid
                .antMatchers("/druid/**").permitAll()
                .anyRequest().authenticated()// 其他 url 需要身份认证
                .and().httpBasic().authenticationEntryPoint(new AppAuthenticationEntryPoint())
                .and()
                .formLogin().loginProcessingUrl("/api/user/login")  //开启登录,如果不指定登录路径(即输入用户名和密码表单提交的路径)，则会默认为spring securtiy的内部定义的路径
//                .successHandler(successAuthenticationHandler)
//                .successForwardUrl("/user/welcome")//
//                .failureHandler(failureAuthenticationHandler)// 遇到用户名或密码不正确/用户被锁定等情况异常，会交给此handler处理
                .permitAll()
                //登陆成功，返回json
                .successHandler(new AppAuthenticationSuccessHandler() )
                .failureHandler(new AppAuthenticationFailureHandler())

                .and()
                .logout()
                .logoutUrl("/api/user/logout")//退出操作，其实也有一个handler，如果没其他业务逻辑，可以默认为spring security的handler
//                //注销成功，重定向到该路径下
//                .logoutSuccessUrl("/api/user/login")
                .permitAll()
                .logoutSuccessHandler(new AppAuthenticationLogoutHandler())
                .and()
                .exceptionHandling();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // 密码加密方式
        return new BCryptPasswordEncoder();
    }
}
