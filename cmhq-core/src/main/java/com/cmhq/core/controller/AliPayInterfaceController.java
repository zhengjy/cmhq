package com.cmhq.core.controller;

import com.cmhq.core.dao.FaRechargeDao;
import com.cmhq.core.model.FaRechargeEntity;
import com.cmhq.core.service.FaRechargeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.APIResponse;
import me.zhengjie.annotation.AnonymousAccess;
import me.zhengjie.annotation.Log;
import me.zhengjie.domain.AlipayConfig;
import me.zhengjie.domain.vo.TradeVo;
import me.zhengjie.service.AliPayService;
import me.zhengjie.utils.AliPayStatusEnum;
import me.zhengjie.utils.AlipayUtils;
import me.zhengjie.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

/**
 * Created by Jiyang.Zheng on 2024/4/9 17:50.
 */
@RestController
@RequestMapping("external/aliPay/")
@Api(value = "外部接口-支付宝", tags = {"外部接口-支付宝"})
@Slf4j
public class AliPayInterfaceController {
    @Autowired
    private AliPayService alipayService;
    @Autowired
    private AlipayUtils alipayUtils;
    @Autowired
    private FaRechargeDao faRechargeDao;
    @Autowired
    private FaRechargeService faRechargeService;

    @Log("支付宝PC网页支付")
    @ApiOperation("支付宝PC网页支付")
    @PostMapping(value = "/toPayAsPC")
    @AnonymousAccess
    @Transactional
    public APIResponse<String> toPayAsPc(@Validated @RequestBody TradeVo trade) throws Exception {
        AlipayConfig aliPay = alipayService.find();
        trade.setOutTradeNo(alipayUtils.getOrderCode());
        String payUrl = alipayService.toPayAsPc(aliPay, trade);
        FaRechargeEntity faRecharge = new FaRechargeEntity();
        faRecharge.setCid(SecurityUtils.getCurrentCompanyId());
        faRecharge.setMoney(Double.parseDouble(trade.getTotalAmount()));
        faRecharge.setStatus(0);
        faRecharge.setCreateTime(new Date());
        faRecharge.setUpdateTime(new Date());
        faRecharge.setDelkid(0);
        faRecharge.setXdelkid(0);
        faRechargeDao.insert(faRecharge);
        return APIResponse.success(payUrl);
    }

    @ApiIgnore
    @GetMapping("/return")
    @AnonymousAccess
    @ApiOperation("支付之后跳转的链接")
    public ResponseEntity<String> returnPage(HttpServletRequest request, HttpServletResponse response) {
        AlipayConfig alipay = alipayService.find();
        response.setContentType("text/html;charset=" + alipay.getCharset());
        //内容验签，防止黑客篡改参数
        if (alipayUtils.rsaCheck(request, alipay)) {
            //商户订单号
            String outTradeNo = new String(request.getParameter("out_trade_no").getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
            //支付宝交易号
            String tradeNo = new String(request.getParameter("trade_no").getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
            System.out.println("商户订单号" + outTradeNo + "  " + "第三方交易号" + tradeNo);


            faRechargeService.applySuccessHandle(outTradeNo,tradeNo);
            // 根据业务需要返回数据，这里统一返回OK
            return new ResponseEntity<>("payment successful", HttpStatus.OK);
        } else {
            // 根据业务需要返回数据
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @ApiIgnore
    @RequestMapping("/notify")
    @AnonymousAccess
    @ApiOperation("支付异步通知(要公网访问)，接收异步通知，检查通知内容app_id、out_trade_no、total_amount是否与请求中的一致，根据trade_status进行后续业务处理")
    @Transactional
    public ResponseEntity<Object> notify(HttpServletRequest request) {
        AlipayConfig alipay = alipayService.find();
        Map<String, String[]> parameterMap = request.getParameterMap();
        //内容验签，防止黑客篡改参数
        if (alipayUtils.rsaCheck(request, alipay)) {
            //交易状态
            String tradeStatus = new String(request.getParameter("trade_status").getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
            // 商户订单号
            String outTradeNo = new String(request.getParameter("out_trade_no").getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
            //支付宝交易号
            String tradeNo = new String(request.getParameter("trade_no").getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
            //付款金额
            String totalAmount = new String(request.getParameter("total_amount").getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
            //验证
            if (tradeStatus.equals(AliPayStatusEnum.SUCCESS.getValue()) || tradeStatus.equals(AliPayStatusEnum.FINISHED.getValue())) {
                faRechargeService.applySuccessHandle(outTradeNo,tradeNo);
            }else {
                log.error("支付宝返回非支付成功状态 {},{}",outTradeNo,totalAmount);
            }
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}