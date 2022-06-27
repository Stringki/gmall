package com.atguigu.gmall.cart.service;

import com.atguigu.gmall.model.cart.CartInfo;

/**
 * 数据库异步操作
 */
public interface AsyncCartInfoService {

    /**
     * 新增购物车
     * @param cartInfo
     * @return
     */
    void addCart(CartInfo cartInfo);

    /**
     * 修改购物车数量
     * @param cartInfo
     * @return
     */
    void updateCart(CartInfo cartInfo);

}
