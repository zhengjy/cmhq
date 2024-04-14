package com.cmhq.core.config;

import com.cmhq.core.util.ContextHolder;
import me.zhengjie.utils.SecurityUtils;
import org.springframework.stereotype.Service;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by Jiyang.Zheng on 2024/4/7 9:03.
 */
@Service
public class ContextHolderFilter  implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
//        if (SecurityUtils.isPlatformCompany()){
//            ContextHolder.setPlatform("company");
//        }else {
//            ContextHolder.setPlatform("admin");
//        }
        filterChain.doFilter(servletRequest, servletResponse);
//        ContextHolder.clearPlatform();

    }
}
