package com.atguigu.gmall.cart.feign;

import com.atguigu.gmall.model.cart.CartInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "service-cart", path = "/api/cart")
public interface CartFeign {

    @GetMapping(value = "/getCartList")
    List<CartInfo> getCartList();
}
