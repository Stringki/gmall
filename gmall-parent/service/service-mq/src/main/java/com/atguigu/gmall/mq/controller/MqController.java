package com.atguigu.gmall.mq.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.service.RabbitService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping(value = "/rabbit")
public class MqController {

    @Resource
    private RabbitService rabbitService;

    /**
     * 测试发送消息
     * @return
     */
    @GetMapping(value = "/sendMassage")
    public Result sendMassage(){
        rabbitService.sendMessage("exchange.confirm", "routing.confirm", "测试消息--我是一只小兔子");
        return Result.ok("消息发送成功！！！");
    }

    @GetMapping(value = "/time")
    public Result time(){
        rabbitService.sendMessage("dead_exchange", "dead_routing", "测试延迟消息", "10000");
        return Result.ok("延迟消息发送完成！！！");
    }

}
