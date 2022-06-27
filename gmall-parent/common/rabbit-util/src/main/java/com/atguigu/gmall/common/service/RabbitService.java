package com.atguigu.gmall.common.service;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;

/**
 * 消息服务发送类
 */
@Service
public class RabbitService {

    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送普通测试消息
     * @param exchange
     * @param routingKey
     * @param message
     * @return
     */
    public Boolean sendMessage(String exchange, String routingKey, Object message){
        rabbitTemplate.convertAndSend(exchange,routingKey,message);
        return true;
    }

    /**
     * 发送带有有效期的消息
     * @param exchange
     * @param routingKey
     * @param message
     * @param time
     * @return
     */
    public boolean sendMessage(String exchange, String routingKey, Object message, @NotNull String time){
        rabbitTemplate.convertAndSend(exchange, routingKey, message, new MessagePostProcessor() {
            /**
             * 设置消息的有效期
             * @param message
             * @return
             * @throws AmqpException
             */
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                //获取消息的属性
                MessageProperties messageProperties = message.getMessageProperties();
                //设置消息的过期时间---单位（ms）
                messageProperties.setExpiration(time);
                return message;
            }
        });
        return true;
    }

}
