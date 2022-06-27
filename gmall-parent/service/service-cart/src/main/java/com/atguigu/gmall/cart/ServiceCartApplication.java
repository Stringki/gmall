package com.atguigu.gmall.cart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan("com.atguigu.gmall")
@EnableFeignClients({"com.atguigu.gmall.product.feign","com.atguigu.gmall.user.feign"})
@EnableAsync
@ServletComponentScan//开启过滤器功能
public class ServiceCartApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceCartApplication.class,args);
    }
}
