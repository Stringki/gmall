package com.atguigu.gmall.list.feign;

import com.atguigu.gmall.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "service-list", path = "/api/list")
public interface ListFeign {
    /**
     * 上架商品
     * @param skuId
     * @return
     */
    @GetMapping("/upperGoods/{skuId}")
    Result upperGoods(@PathVariable("skuId") Long skuId);

    /**
     * 下架商品
     * @param skuId
     * @return
     */
    @GetMapping("/lowerGoods/{skuId}")
    Result lowerGoods(@PathVariable("skuId") Long skuId);

    /**
     * 更新商品incrHotScore
     *
     * @param skuId
     * @return
     */
    @GetMapping("/incrHotScore/{skuId}")
    Result incrHotScore(@PathVariable("skuId") Long skuId);

    /**
     * 搜索商品
     * @param searchMap
     * @return
     */
    @GetMapping(value = "/search")
    Map<String,Object> search(@RequestParam Map<String,String> searchMap);
}
