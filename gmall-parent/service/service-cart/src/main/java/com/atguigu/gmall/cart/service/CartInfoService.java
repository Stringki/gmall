package com.atguigu.gmall.cart.service;

import com.atguigu.gmall.model.cart.CartInfo;

import java.util.List;

public interface CartInfoService {

    /**
     * 购物车操作
     * @param skuId
     * @param userId
     * @param num
     * @return
     */
    CartInfo CartInfoCrud(Long skuId,String userId,Integer num);

    /**
     * 新增购物车
     * @param skuId
     * @param userId
     * @param num
     * @return
     */
    CartInfo addCart(Long skuId,String userId,Integer num);

    /**
     * 根据用户Id查询对相应购物车信息
     * @param userId
     * @return
     */
    List<CartInfo> getCartInfo(String userId);

    /**
     * 合并购物车
     * @param cartInfoList
     * @param userId
     */
    void mergeCartInfo(List<CartInfo> cartInfoList ,String userId);

    /**
     * 删除用户购物车
     * @param skuId
     * @param userId
     */
    void delCart(Long skuId,String userId);

    /**
     * 查询用户当前需要购买的商品的列表-Feign
     *
     * @param username
     * @return
     */
    List<CartInfo> getCartList(String username);

}
