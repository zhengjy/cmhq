package com.cmhq.core.app.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cmhq.core.app.dao.FaSaleUserDao;
import com.cmhq.core.app.dao.FaSaleUserMoneyDao;
import com.cmhq.core.app.dao.FaSaleWithdrawDao;
import com.cmhq.core.app.dao.HomeDao;
import com.cmhq.core.app.model.FaUserEntity;
import com.cmhq.core.app.model.FaUserMoneyEntity;
import com.cmhq.core.app.model.FaWithdrawEntity;
import com.cmhq.core.app.model.param.HomeQuery;
import com.cmhq.core.app.model.rsp.HomeCompanyRsp;
import com.cmhq.core.app.model.rsp.UserYongjinRsp;
import com.cmhq.core.app.service.UserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.APIResponse;
import me.zhengjie.QueryResult;
import me.zhengjie.modules.security.config.AuthUserDto;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/user/")
@RequiredArgsConstructor
@Api(tags = "用户中心")
public class FaSaleUserController {
    @Autowired
    private UserService userService;
    @Autowired
    private FaSaleUserDao faSaleUserDao;
    @Autowired
    private FaSaleUserMoneyDao faSaleUserMoneyDao;
    @Autowired
    private FaSaleWithdrawDao faSaleWithdrawDao;
    @Autowired
    private HomeDao homeDao;
    @ApiOperation("佣金")
    @GetMapping(value = "/yongjin")
    public APIResponse yongjin(@ModelAttribute HomeQuery query) {
        UserYongjinRsp rsp = new UserYongjinRsp();
        FaUserEntity faUser = userService.queryCurrentUser();
        rsp.setKetixian(faUser.getYongjin() - faUser.getTixian());
        HomeQuery homeQuery = new HomeQuery();
        homeQuery.setUserId(faUser.getId());
        List<HomeCompanyRsp> list = Optional.of(homeDao.selectCompanyList(homeQuery)).orElse(Collections.emptyList());
        rsp.setYugufenyong(BigDecimal.valueOf(list.stream().mapToDouble(HomeCompanyRsp::getYugufenyong).sum()).setScale(2, RoundingMode.HALF_UP).doubleValue());

        rsp.setYitixian(faUser.getTixian());


        LambdaQueryWrapper<FaWithdrawEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotEmpty(query.getSd())){
            lambdaQueryWrapper.eq(FaWithdrawEntity::getCreateTime,query.getSd());
            lambdaQueryWrapper.eq(FaWithdrawEntity::getCreateTime,query.getEd());
        }
        lambdaQueryWrapper.eq(FaWithdrawEntity::getUserId,faUser.getId());
        PageHelper.startPage(query.getPage(), query.getLimit());
        List<FaWithdrawEntity> list2 = faSaleWithdrawDao.selectList(lambdaQueryWrapper);
        PageInfo<FaWithdrawEntity> page = new PageInfo<>(list2);
        QueryResult<FaWithdrawEntity> queryResult = new QueryResult<>();
        queryResult.setItems(list2);
        queryResult.setTotal(page.getTotal());
        rsp.setList(list2);
        return APIResponse.success(rsp);
    }


    @ApiOperation("个人中心")
    @GetMapping(value = "/index")
    public APIResponse index(HttpServletRequest request) {
        FaUserEntity faUser = userService.queryCurrentUser();
        return APIResponse.success(faUser);
    }

    @ApiOperation("修改密码")
    @PostMapping(value = "/resetpwd")
    public APIResponse resetpwd(@RequestBody AuthUserDto dto) {
        FaUserEntity faUser = userService.queryCurrentUser();
        FaUserEntity u = new FaUserEntity();
        u.setId(faUser.getId());
        u.setPassword(dto.getNewpassword());
        faSaleUserDao.updateById(u);
        return APIResponse.success(u);
    }

    @Transactional
    @ApiOperation("提现")
    @PostMapping(value = "/withdrawal")
    public APIResponse withdrawal(@RequestBody FaWithdrawEntity entity) {
        FaWithdrawEntity e = new FaWithdrawEntity();
        e.setZhiAccount(entity.getZhiAccount());
        e.setZhiName(entity.getZhiName());
        e.setMoney(entity.getMoney());
        FaUserEntity faUser = userService.queryCurrentUser();
        e.setUserId(faUser.getId());
        e.setType(2);
        faSaleWithdrawDao.insert(e);


        FaUserMoneyEntity um = new FaUserMoneyEntity();
        um.setUserId(faUser.getId());
        um.setMoney(entity.getMoney());
        um.setType(2);
        um.setBefore(faUser.getMoney());
        um.setIsFenxiao("1");
        um.setAfterMoney(faUser.getMoney()-entity.getMoney());
        faSaleUserMoneyDao.insert(um);

        FaUserEntity u = new FaUserEntity();
        u.setId(faUser.getId());
        u.setMoney(faUser.getMoney()-entity.getMoney());
        u.setZhiAccount(entity.getZhiAccount());
        u.setZhiName(entity.getZhiName());
        faSaleUserDao.updateById(u);

        return APIResponse.success();
    }
}
