package com.atguigu.gmall.item.feign;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.item.feign.fallback.ItemFeignClientFallback;
import com.atguigu.gmall.model.product.SkuImage;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;

/**
 * 商品详情微服务远程调用feign管理接口
 */
@FeignClient(name = "service-item", path = "/item/goods", fallback = ItemFeignClientFallback.class)
public interface ItemFeign {

    /**
     * 查询sku的详细信息,用于生成sku的详情页面
     * @param skuId
     * @return
     */
    @GetMapping(value = "/detail/{skuId}")
    public Map<String, Object> getGoodsItem(@PathVariable(value = "skuId") Long skuId);

}
