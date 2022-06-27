package com.atguigu.gmall.item.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.item.service.ItemService;
import com.atguigu.gmall.list.feign.ListFeign;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.product.feign.ProductFeign;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@Service
public class ItemServiceImpl implements ItemService {

    @Resource
    private ProductFeign productFeign;

    @Resource
    private ListFeign listFeign;


    /**
     * 获取商品的详情
     *
     * @param skuId
     * @return
     */
    @Override
    public Map<String, Object> getGoodsItem(Long skuId) {
        //初始化返回值
        Map<String, Object> map = new HashMap<>();
        //任务一执行
        CompletableFuture<SkuInfo> skuInfoCompletableFuture = CompletableFuture.supplyAsync(() -> {
            //获取商品的详情
            SkuInfo skuInfo = productFeign.getSkuInfo(skuId);
            map.put("skuInfo", skuInfo);
            return skuInfo;
        });
        //任务二与任务一并行
        CompletableFuture<Void> priceFuture = CompletableFuture.runAsync(() -> {
            //获取商品的价格
            BigDecimal price = productFeign.getPrice(skuId);
            map.put("price", price);
        });
        //任务三与任务一并行
        CompletableFuture<Void> imageFuture = CompletableFuture.runAsync(() -> {
            //获取商品的图片
            List<SkuImage> skuImages = productFeign.getSkuImage(skuId);
            map.put("skuImage", skuImages);
        });
        //依赖于任务一,执行任务四
        CompletableFuture<Void> categoryFuture = skuInfoCompletableFuture.thenAccept(new Consumer<SkuInfo>() {
            @Override
            public void accept(SkuInfo skuInfo) {
                //查询分类信息
                BaseCategoryView category = productFeign.getCategory(skuInfo.getCategory3Id());
                map.put("category", category);
            }
        });
        //依赖于任务一,执行任务五
        CompletableFuture<Void> spuSaleAttrsFuture = skuInfoCompletableFuture.thenAccept(new Consumer<SkuInfo>() {
            @Override
            public void accept(SkuInfo skuInfo) {
                //查询销售属性的信息
                List<SpuSaleAttr> spuSaleAttrs = productFeign.getBySpuIdAndSkuId(skuId, skuInfo.getSpuId());
                map.put("spuSaleAttrs", spuSaleAttrs);
            }
        });
        //依赖于任务一,执行任务六
        CompletableFuture<Void> skuSaleAttrBySpuIdFuture = skuInfoCompletableFuture.thenAccept(new Consumer<SkuInfo>() {
            @Override
            public void accept(SkuInfo skuInfo) {
                //获取键值对
                Map skuSaleAttrBySpuId = productFeign.getSkuSaleAttrBySpuId(skuInfo.getSpuId());
                map.put("skuSaleAttrBySpuId", JSONObject.toJSONString(skuSaleAttrBySpuId));
            }
        });
        //保证所有的任务都执行完成,才返回map对象
        CompletableFuture.allOf(skuInfoCompletableFuture,
                priceFuture,
                categoryFuture,
                imageFuture,
                spuSaleAttrsFuture,
                skuSaleAttrBySpuIdFuture).join();
        //异步更新热点值
        CompletableFuture.runAsync(()->{
            listFeign.incrHotScore(skuId);
        });
        //返回
        return map;
    }
}
