package com.atguigu.gmall.order.feign;

import com.atguigu.gmall.model.order.OrderInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "service-order", path = "/api/order")
public interface OrderFeign {

    /**
     * 根据订单ID查询订单详情
     * @param orderId
     * @return
     */
    @GetMapping(value = "/getOrDeInfo/{orderId}")
    public OrderInfo getOrDeInfo(@PathVariable(value = "orderId") Long orderId);

}
