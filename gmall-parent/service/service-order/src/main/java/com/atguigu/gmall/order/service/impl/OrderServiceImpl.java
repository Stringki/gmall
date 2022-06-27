package com.atguigu.gmall.order.service.impl;

import com.atguigu.gmall.common.constant.MqConst;
import com.atguigu.gmall.common.service.RabbitService;
import com.atguigu.gmall.model.enums.ProcessStatus;
import com.atguigu.gmall.model.order.OrderDetail;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.order.mapper.OrderDetailMapper;
import com.atguigu.gmall.order.mapper.OrderInfoMapper;
import com.atguigu.gmall.order.service.OrderService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 订单服务类
 */
@Transactional
@Service
public class OrderServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo> implements OrderService {

    @Resource
    private OrderDetailMapper orderDetailMapper;

    @Resource
    private RabbitService rabbitService;
    /**
     *新增订单
     * @param orderInfo
     * @return
     */
    @Override
    public Long saveOrderInfo(OrderInfo orderInfo) {
        //计算总价
        orderInfo.sumTotalAmount();
        //生成交易订单号
        String outTradeNo = "AtGuiGu" + UUID.randomUUID().toString();
        orderInfo.setOutTradeNo(outTradeNo);
        //生成订单详情描述
        orderInfo.setTradeBody("国美商城交易订单");
        //生成订单创建时间
        orderInfo.setCreateTime(new Date());
        //生成订单支付过期时间
        orderInfo.setExpireTime(new Date(System.currentTimeMillis()+1800000));
        //设置支付进度状态
        orderInfo.setProcessStatus(ProcessStatus.UNPAID.name());
        //保存订单数据
        save(orderInfo);
        //补全订单详情表中的数据
        List<OrderDetail> orderDetailList = orderInfo.getOrderDetailList();
        orderDetailList.stream().map(o->{
            o.setOrderId(orderInfo.getId());
            orderDetailMapper.insert(o);
            return o;
        }).collect(Collectors.toList());
        //发送延迟消息
        rabbitService.sendMessage("order.dead.exchange",
                                 "order.dead.routing",
                            orderInfo.getId(), MqConst.DELAY_TIME);
        return orderInfo.getId();
    }

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 生产流水号
     * @param userId
     * @return
     */
    @Override
    public String getTradeNo(String userId) {
        //以用户id+前后缀为key
        String key = "user:" + userId + ":tradeCode";
        //以随机串作为流程号
        String value = UUID.randomUUID().toString();
        //生成完成保存到Redis
        redisTemplate.boundValueOps(key).set(value);
        //返回流水号
        return value;
    }

    /**
     * 比较流水号
     * @param userId 获取缓存中的流水号
     * @param tradeCodeNo   页面传递过来的流水号
     * @return
     */
    @Override
    public boolean checkTradeCode(String userId, String tradeCodeNo) {
        //以用户id+前后缀为key
        String key = "user:" + userId + ":tradeCode";
        //获取已经存储的流水号信息
        String u = (String)redisTemplate.boundValueOps(key).get();
        return u.equals(tradeCodeNo);
    }

    /**
     *删除流水号
     * @param userId
     */
    @Override
    public void deleteTradeNo(String userId) {
        //以用户id+前后缀为key
        String key = "user:" + userId + ":tradeCode";
        redisTemplate.delete(key);
    }

    /**
     * 根据订单ID修改订单状态
     * @param orderId
     * @param processStatus
     */
    @Override
    public void execExpiredOrder(Long orderId, ProcessStatus processStatus) {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setId(orderId);
        orderInfo.setProcessStatus(processStatus.name());
        orderInfo.setOrderStatus(processStatus.getOrderStatus().name());
        updateById(orderInfo);
    }

    /**
     * 根据订单ID查询订单信息
     * @param orderId
     * @return
     */
    @Override
    public OrderInfo getOrDeInfo(Long orderId) {
        //查询订单信息
        OrderInfo orderInfo = getById(orderId);
        //查询订单详情列表
        List<OrderDetail> orderDetails =
                orderDetailMapper.selectList(new LambdaQueryWrapper<OrderDetail>().eq(OrderDetail::getOrderId, orderId));
        orderInfo.setOrderDetailList(orderDetails);
        return orderInfo;
    }
}
