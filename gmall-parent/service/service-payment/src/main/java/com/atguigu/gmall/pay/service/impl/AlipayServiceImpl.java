package com.atguigu.gmall.pay.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.order.feign.OrderFeign;
import com.atguigu.gmall.pay.service.AlipayService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;

@Service
public class AlipayServiceImpl implements AlipayService {

    @Resource
    private AlipayClient alipayClient;

    @Resource
    private OrderFeign orderFeign;

    //同步回调地址
    @Value("${alipay.pay.return_payment_url}")
    private String returnPaymentUrl;
    //异步回调地址
    @Value("${alipay.pay.notify_payment_url}")
    private String notifyPaymentUrl;

    /**
     * 支付下单
     *
     * @param orderId
     * @return
     */
    @Override
    public String createAliPay(Long orderId) {
        OrderInfo orDeInfo = orderFeign.getOrDeInfo(orderId);
        String outTradeNo = orDeInfo.getOutTradeNo();//通过订单ID获取到订单编号
        //BigDecimal totalAmount = orDeInfo.getTotalAmount();//获取到订单交易总金额
        //创建支付请求对象
        AlipayTradePagePayRequest payRequest = new AlipayTradePagePayRequest();
        //设置同步回调地址
        payRequest.setReturnUrl(returnPaymentUrl);
        //设置异步回调地址
        payRequest.setNotifyUrl(notifyPaymentUrl);
        //定义biz_content
        HashMap<String, Object> map = new HashMap<>();
        map.put("out_trade_no","ATGUIGU1589251372883520");//商户订单号
        map.put("total_amount",168);//订单总金额
        map.put("product_code","FAST_INSTANT_TRADE_PAY");//产品码
        map.put("subject","这是一个测试商品");//商品描述
        //把biz_content放入请求对象
        payRequest.setBizContent(JSONObject.toJSONString(map));
        String result = null;
        //发送请求
        try {
            result = alipayClient.pageExecute(payRequest).getBody();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
