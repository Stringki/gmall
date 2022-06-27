package com.atguigu.gmall.pay.service;

import com.atguigu.gmall.model.order.OrderInfo;

import java.util.Map;

/**
 * 支付接口类
 */
public interface PayService {

    /**
     * 创建支付的二维码地址
     * @param orderId
     * @param totalMoney
     * @return
     */
     Map createPayCode(Long orderId, Integer totalMoney);

    /**
     * 保存交易记录
     * @param orderInfo
     * @param paymentType:(支付类型  1：微信，2：支付宝)
     */
     void savePaymentInfo(OrderInfo orderInfo,String paymentType);

}
