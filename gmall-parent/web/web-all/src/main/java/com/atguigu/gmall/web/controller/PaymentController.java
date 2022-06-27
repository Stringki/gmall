package com.atguigu.gmall.web.controller;

import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.order.feign.OrderFeign;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Controller
public class PaymentController {

    @Resource
    private OrderFeign orderFeign;

    /**
     * 支付页面
     * @param request
     * @param model
     * @return
     */
    @GetMapping("/pay.html")
    public String success(HttpServletRequest request, Model model){
        String orderId = request.getParameter("orderId");
        OrderInfo orDeInfo = orderFeign.getOrDeInfo(Long.parseLong(orderId));
        model.addAttribute("orderInfo",orDeInfo);
        return "payment/pay";
    }
}
