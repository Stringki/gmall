package com.atguigu.gmall.order.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderCancleMqConfig {
    /**
     * 定义死信队列
     */
    @Bean("OrderDeadQueue")
    public Queue deadQueue(){
        return QueueBuilder.durable("order.dead.queue")
                .withArgument("x-dead-letter-exchange", "order.normal.exchange")
                .withArgument("x-dead-letter-routing-key", "order.normal.routing")
                .build();
    }

    /**
     * 定义正常队列
     */
    @Bean("OrderNormalQueue")
    public Queue normalQueue(){
        return QueueBuilder.durable("order.normal.queue").build();
    }

    /**
     * 定义死信交换机
     */
    @Bean("OrderDeadExchange")
    public Exchange deadExchange(){
        return ExchangeBuilder.directExchange("order.dead.exchange").build();
    }

    /**
     * 正常交换机
     */
    @Bean("OrderNormalExchange")
    public Exchange normalExchange(){
        return ExchangeBuilder.directExchange("order.normal.exchange").build();
    }

    /**
     * 定义绑定:死信交换机和死信队列绑定
     */
    @Bean
    public Binding deadBinding(@Qualifier("OrderDeadQueue") Queue deadQueue,
                               @Qualifier("OrderDeadExchange") Exchange deadExchange){
        return BindingBuilder.bind(deadQueue).to(deadExchange).with("order.dead.routing").noargs();
    }

    /**
     * 定义绑定:正常交换机和正常队列绑定
     */
    @Bean
    public Binding normalBinding(@Qualifier("OrderNormalQueue") Queue normalQueue,
                                 @Qualifier("OrderNormalExchange") Exchange normalExchange){
        return BindingBuilder.bind(normalQueue).to(normalExchange).with("order.normal.routing").noargs();
    }
}
