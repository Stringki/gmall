package com.atguigu.gmall.pay.service;

public interface AlipayService {
    /**
     * 支付下单
     * @param orderId
     * @return
     */
    String createAliPay(Long orderId);
}
