package com.atguigu.gmall.list.service;

import com.atguigu.gmall.model.list.Goods;

import java.util.Map;

public interface SearchService {

    /**
     * 上架商品
     * @param skuId
     */
    Goods upperGoods(Long skuId);

    /**
     * 下架商品
     * @param skuId
     */
    void lowerGoods(Long skuId);

    /**
     * 更新热点
     * @param skuId
     */
    void incrHotScore(Long skuId);

    /**
     * 搜索商品
     * @param searchMap
     * @return
     */
     Map<String,Object> search(Map<String,String> searchMap);
}
