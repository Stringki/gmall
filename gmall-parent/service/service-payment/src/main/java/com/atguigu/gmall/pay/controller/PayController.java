package com.atguigu.gmall.pay.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.pay.service.PayService;
import com.github.wxpay.sdk.WXPayUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/pay")
public class PayController {

    @Resource
    private PayService payService;

    /**
     * 获取支付的二维码地址
     * @param orderId
     * @param totalMoney
     * @return
     */
    @GetMapping(value = "/createPayCode")
    public Result createPayCode(Long orderId, Integer totalMoney){
        Map result = payService.createPayCode(orderId, totalMoney);
        return Result.ok(result);
    }

    /**
     * 微信回调地址
     * @param request
     * @return
     */
    @RequestMapping(value = "/order/notify")
    public String notifyAddress(HttpServletRequest request) throws Exception {
        //获取微信的通知结果
        ServletInputStream WxMessage = request.getInputStream();
        //转换为输出流
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        byte[] bytes = new byte[1024];
        int len = 0;
        //读取数据
        while ((len = WxMessage.read(bytes)) != -1){
            stream.write(bytes,0,len);
        }
        //输出流转换为对象
        String s = new String(stream.toByteArray(), "UTF-8");
        //输出xml格式的字符串
        System.out.println(s);
        Map<String, String> map = WXPayUtil.xmlToMap(s);
        //输出map类型的数据
        System.out.println(map);
        //修改订单的状态了--发送一条支付结果的消息
        //rabbitService.sendMessage("nomal_pay_exchange", "nomal.pay" , JSONObject.toJSONString(map));
        //返回微信收到了结果
        Map<String, String> result = new HashMap<>();
        result.put("return_code","SUCCESS");
        result.put("return_msg","OK");
        //返回
        return WXPayUtil.mapToXml(result);
    }

}
