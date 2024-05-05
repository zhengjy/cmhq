package com.cmhq.core.controller;

import me.zhengjie.APIResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BaseController  {

//    @ExceptionHandler
//    @ResponseBody
//    public APIResponse exception(HttpServletRequest request, HttpServletResponse response, Exception e) {
//        log.error("统一处理controller异常");
//
//        if (e instanceof RuntimeException) {//BizException自定义异常
//            BizException be = (BizException) e;
//            log.error("BaseController.BizException! code:{}, msg:{}",
//                    be.getErrorCode(), be.getMessage());
//            return new Result(be.getErrorCode(), be.getMessage(), new Object());
//        } else {
//            log.error("BaseController.Exception! ", e);
//            return Result.buildFail(OperateStatus.SERVER_EXCEPTION);
//        }
//        return APIResponse.fail("")

    }

