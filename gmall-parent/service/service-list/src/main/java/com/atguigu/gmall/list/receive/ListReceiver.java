package com.atguigu.gmall.list.receive;

import com.atguigu.gmall.common.constant.MqConst;
import com.atguigu.gmall.list.service.SearchService;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

@Component
public class ListReceiver {

    @Resource
    private SearchService searchService;

    /**
     * 商品上架
     * @param skuId
     * @throws IOException
     */
    @RabbitListener(queues = MqConst.QUEUE_GOODS_UPPER)
    public void upperGoods(Long skuId, Message message, Channel channel) throws IOException{
        if (null != skuId){
            searchService.upperGoods(skuId);
        }
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
    }

    /**
     * 商品下架
     * @param skuId
     * @param message
     * @param channel
     * @throws IOException
     */
    @RabbitListener(queues = MqConst.QUEUE_GOODS_LOWER)
    public void lowerGoods(Long skuId,Message message,Channel channel) throws IOException{
        if (null != skuId){
            searchService.lowerGoods(skuId);
        }
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
    }
}
