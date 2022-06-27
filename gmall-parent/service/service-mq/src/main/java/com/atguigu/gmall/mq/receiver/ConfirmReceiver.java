package com.atguigu.gmall.mq.receiver;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
@Component
@Slf4j
public class ConfirmReceiver {

    /**
     * 消息监听
     * @param channel
     * @param message
     */
    @RabbitListener(queues = "queue.confirm")
    public void receiverMessage(Channel channel, Message message){
        try {
            log.info("接收的消息为:"+ new String(message.getBody(),"UTF-8"));
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        } catch (Exception e) {
            e.printStackTrace();
            if (message.getMessageProperties().getRedelivered()){
                log.info("消息已经被拒绝接收过一次,将不会进行第二次拒绝接收");

                try {
                    //拒绝接收消息,直接丢弃消息
                    /**
                     * 1.消息的编号
                     * 2.是否将消息从新放回队列: true消息将被放回消息队列, false消息直接丢弃掉(成为死信)
                     */
                    channel.basicReject(message.getMessageProperties().getDeliveryTag(),false);
                } catch (IOException ex) {
                    ex.printStackTrace();
                    log.error("拒绝消息发生异常,消息的编号为:" + message.getMessageProperties().getDeliveryTag());
                }
            }
        }
    }

    /**
     * 接收延迟消息
     * @param channel
     * @param message
     */
    @RabbitListener(queues = "normal_queue")
    public void delayMessage(Channel channel, Message message) throws Exception{
        log.info("收到的消息时间为:" + System.currentTimeMillis());
        log.info("收到的消息内容为:" + new String(message.getBody(), "UTF-8"));
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

}
