package com.atguigu.gmall.pay.service.impl;

import com.atguigu.gmall.common.util.HttpClient;
import com.atguigu.gmall.model.enums.PaymentStatus;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.payment.PaymentInfo;
import com.atguigu.gmall.pay.mapper.PaymentInfoMapper;
import com.atguigu.gmall.pay.service.PayService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.wxpay.sdk.WXPayUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class PayServiceImpl implements PayService {

    @Value("${weixin.pay.appid}")
    private String appId;
    @Value("${weixin.pay.partner}")
    private String partner;
    @Value("${weixin.pay.partnerkey}")
    private String partnerkey;
    @Value("${weixin.pay.notifyUrl}")
    private String notifyUrl;

    /**
     *创建支付的二维码地址
     * @param orderId
     * @param totalMoney
     * @return
     */
    @Override
    public Map createPayCode(Long orderId, Integer totalMoney) {
        //定义请求的url
        String url = "https://api.mch.weixin.qq.com/pay/unifiedorder";
        //包装请求的参数
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("appid", appId);
        paramMap.put("mch_id",partner);
        paramMap.put("nonce_str", WXPayUtil.generateNonceStr());
        paramMap.put("body","测试商品");
        paramMap.put("out_trade_no", orderId + "");
        paramMap.put("total_fee",totalMoney + "");
        paramMap.put("spbill_create_ip","192.168.200.1");
        paramMap.put("notify_url",notifyUrl);
        paramMap.put("trade_type","NATIVE");
        return postToWxServer(url,paramMap);
    }

    /**
     * 向微信端发起请求，获取结果
     * @param url
     * @param paramMap
     * @return
     */
    private Map postToWxServer(String url, Map<String, String> paramMap) {
        try {
            String XmlParam = WXPayUtil.generateSignedXml(paramMap, partnerkey);
            //发起请求
            HttpClient httpClient = new HttpClient(url);
            httpClient.setHttps(true);
            httpClient.setXmlParam(XmlParam);
            httpClient.post();
            //解析结果
            String content = httpClient.getContent();
            Map<String, String> map = WXPayUtil.xmlToMap(content);
            if(map.get("return_code").equals("SUCCESS") && map.get("result_code").equals("SUCCESS")){
                //返回结果
                return map;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Resource
    private PaymentInfoMapper paymentInfoMapper;

    /**
     * 保存交易记录
     *
     * @param orderInfo
     * @param paymentType :(支付类型  1：微信，2：支付宝)
     */
    @Override
    public void savePaymentInfo(OrderInfo orderInfo, String paymentType) {
        //获取订单号
        Long id = orderInfo.getId();
        //查询订单是否保存
        LambdaQueryWrapper<PaymentInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PaymentInfo::getOrderId,id);
        Integer count = paymentInfoMapper.selectCount(wrapper);
        //判断是否保存
        if (count>0){
            //已保存直接返回
            return;
        }
        //未保存构建数据
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setOutTradeNo(orderInfo.getOutTradeNo());
        paymentInfo.setOrderId(orderInfo.getId());
        paymentInfo.setPaymentType(paymentType);
        paymentInfo.setTotalAmount(orderInfo.getTotalAmount());
        paymentInfo.setSubject(orderInfo.getTradeBody());
        paymentInfo.setPaymentStatus(PaymentStatus.UNPAID.name());
        paymentInfo.setCreateTime(new Date());
        //保存数据
        paymentInfoMapper.insert(paymentInfo);
    }
}
