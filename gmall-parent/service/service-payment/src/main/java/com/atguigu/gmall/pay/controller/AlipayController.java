package com.atguigu.gmall.pay.controller;

import com.atguigu.gmall.pay.service.AlipayService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping(value = "/api/payment/alipay")
public class AlipayController {

    @Resource
    private AlipayService alipayService;

    /**
     * 提交支付
     * @param orderId
     * @param response
     * @return
     */
    @RequestMapping(value = "/submit/{orderId}")
    public String submitOrder(@PathVariable(value = "orderId") Long orderId, HttpServletResponse response){
        return alipayService.createAliPay(orderId);
    }

}
