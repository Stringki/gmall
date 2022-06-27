package com.atguigu.gmall.item.service;

import java.util.Map;

public interface ItemService {

    /**
     * 获取商品的详情
     * @param skuId
     * @return
     */
   Map<String, Object> getGoodsItem(Long skuId);

}
