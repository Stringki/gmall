package com.atguigu.gmall.cart.service.impl;

import com.atguigu.gmall.cart.mapper.CartInfoMapper;
import com.atguigu.gmall.cart.service.AsyncCartInfoService;
import com.atguigu.gmall.model.cart.CartInfo;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 数据库异步操作类
 */
@Service
public class AsyncCartInfoServiceImpl implements AsyncCartInfoService {

    @Resource
    private CartInfoMapper cartInfoMapper;

    @Async("threadPoolExecutor")
    @Override
    public void addCart(CartInfo cartInfo) {
        cartInfoMapper.insert(cartInfo);
    }

    @Async("threadPoolExecutor")
    @Override
    public void updateCart(CartInfo cartInfo) {
        cartInfoMapper.updateById(cartInfo);
    }
}
