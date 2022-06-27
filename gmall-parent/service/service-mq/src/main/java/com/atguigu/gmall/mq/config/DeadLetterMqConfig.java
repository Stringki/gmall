package com.atguigu.gmall.mq.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MQ死信队列配置
 */
@Configuration
public class DeadLetterMqConfig {

    /**
     * 定义死信队列
     */
    @Bean("deadQueue")
    public Queue deadQueue(){
        return QueueBuilder.durable("dead_queue")
                .withArgument("x-dead-letter-exchange", "normal_exchange")
                .withArgument("x-dead-letter-routing-key", "normal_routing")
                .build();
    }

    /**
     * 定义正常队列
     */
    @Bean("normalQueue")
    public Queue normalQueue(){
        return QueueBuilder.durable("normal_queue").build();
    }

    /**
     * 定义死信交换机
     */
    @Bean("deadExchange")
    public Exchange deadExchange(){
        return ExchangeBuilder.directExchange("dead_exchange").build();
    }

    /**
     * 定义正常交换机
     * @return
     */
    @Bean("normalExchange")
    public Exchange normalExchange(){
        return ExchangeBuilder.directExchange("normal_exchange").build();
    }

    /**
     * 定义绑定:死信交换机与死信队列绑定
     * @return
     */
    @Bean
    public Binding deadBinding(@Qualifier("deadQueue") Queue deadQueue,
                               @Qualifier("deadExchange") Exchange deadExchange){
        return BindingBuilder.bind(deadQueue).to(deadExchange).with("dead_routing").noargs();
    }

    /**
     * 定义绑定:正常交换机和正常队列绑定
     */
    @Bean
    public Binding normalBinding(@Qualifier("normalQueue") Queue normalQueue,
                                 @Qualifier("normalExchange") Exchange normalExchange){
        return BindingBuilder.bind(normalQueue).to(normalExchange).with("normal_routing").noargs();
    }
}
