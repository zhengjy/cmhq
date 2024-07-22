
package com.cmhq.core.app.controller;

import com.cmhq.core.app.dao.FaSaleUserDao;
import com.cmhq.core.app.model.AuthUserDto;
import com.cmhq.core.app.security.FauserLoginAuthenticationProvider;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.APIResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author Zheng Jie
 * @date 2018-11-23
 * 授权、根据token获取用户详细信息
 */
@Slf4j
@RestController
@RequestMapping("/api/user/")
@RequiredArgsConstructor
@Api(tags = "系统：系统授权接口")
public class LoginController {
    @Resource
    private FaSaleUserDao faSaleUserDao;
    @Resource(name = "fauserLoginAuthenticationProvider")
    private FauserLoginAuthenticationProvider loginAuthenticationProvider;

    @ApiOperation("登录授权")
    @PostMapping(value = "login")
    public APIResponse login(AuthUserDto authUser, HttpServletRequest request) throws Exception {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(authUser.getUsername(), authUser.getPassword());
        Authentication authentication = loginAuthenticationProvider.authenticate(authenticationToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 返回登录信息
        return APIResponse.success(authenticationToken);
    }

    @ApiOperation("获取用户信息")
    @GetMapping(value = "/info")
    public APIResponse getUserInfo() {
        return APIResponse.success(SecurityContextHolder.getContext().getAuthentication());
    }



    @ApiOperation("退出登录")
    @GetMapping(value = "/logout")
    public APIResponse logout(HttpServletRequest request) {
        return APIResponse.success();
    }


}
