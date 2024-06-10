package com.cmhq.app.config;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

@Component
public class AppAuthenticationSuccessHandler implements org.springframework.security.web.authentication.AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws ServletException, IOException {
        httpServletResponse.setContentType("application/json;charset=UTF-8");
        HashMap<String, Object> result = Maps.newHashMap();
        result.put("message", "成功");
        result.put("success", Boolean.TRUE);
        result.put("code", 200);
        httpServletResponse.getWriter().write(JSON.toJSONString(result));
    }
}
