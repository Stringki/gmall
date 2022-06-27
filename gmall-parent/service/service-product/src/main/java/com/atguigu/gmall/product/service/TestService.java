package com.atguigu.gmall.product.service;

/**
 * 分布式锁的案例
 */
public interface TestService {

    /**
     * 在redis中设置值
     */
    public void setRedisKey();
}
