package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.product.service.TestService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class TestServiceImpl implements TestService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 在redis中设置值
     */
    @Override
    public void setRedisKey() {
        RLock lock = redissonClient.getLock("lock");
        try {
            if(lock.tryLock(10, 1 , TimeUnit.SECONDS)) {
                //获取锁成功
                //获取成功,则+1,放回redis
                Integer java0223 = (Integer) redisTemplate.boundValueOps("java0223").get();
                if (java0223 != null) {
                    java0223++;
                    redisTemplate.boundValueOps("java0223").set(java0223);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }

}
