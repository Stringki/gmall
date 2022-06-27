package com.atguigu.gmall.item.feign.fallback;


import com.atguigu.gmall.item.feign.ItemFeign;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 降级类
 */
@Component
public class ItemFeignClientFallback implements ItemFeign {
    /**
     * 查询sku的详细信息,用于生成sku的详情页面
     *
     * @param skuId
     * @return
     */
    @Override
    public Map<String, Object> getGoodsItem(Long skuId) {
        return null;
    }
}
