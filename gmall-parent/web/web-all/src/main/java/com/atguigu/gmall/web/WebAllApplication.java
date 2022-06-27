package com.atguigu.gmall.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients({"com.atguigu.gmall.item.feign",
                    "com.atguigu.gmall.product.feign",
                    "com.atguigu.gmall.list.feign",
                    "com.atguigu.gmall.order.feign"})
@ComponentScan("com.atguigu.gmall")
public class WebAllApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebAllApplication.class, args);
    }
}
