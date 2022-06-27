package com.atguigu.gmall.order.receiver;

import com.atguigu.gmall.model.enums.ProcessStatus;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.order.service.OrderService;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * 取消订单的监听类
 */
@Component
public class OrderReceiver {

    @Resource
    private OrderService orderService;

    public void orderCancel(Long orderId, Message message, Channel channel) throws IOException {
        if (null != orderId){
            //防止重复提交消费订单
            OrderInfo orderInfo = orderService.getById(orderId);
            if (orderInfo != null && orderInfo.getOrderStatus().equals(ProcessStatus.UNPAID.getOrderStatus().name())){
                    // TODO: 2022/6/22
                orderService.execExpiredOrder(orderId,ProcessStatus.CLOSED);
            }
        }
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
    }
}
