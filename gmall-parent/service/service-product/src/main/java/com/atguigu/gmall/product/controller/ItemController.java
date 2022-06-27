package com.atguigu.gmall.product.controller;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.common.cache.GmallCache;
import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/admin/item")
public class ItemController {

    @Resource
    private ItemService itemService;

    /**
     * 查询sku的详情的信息
     * @param skuId
     * @return
     */
    @GmallCache(prefix = RedisConst.SKUKEY_PREFIX)// sku:
    @GetMapping(value = "/getSkuInfo/{skuId}")
    public SkuInfo getSkuInfo(@PathVariable(value = "skuId") Long skuId){

        return itemService.getSkuInfo(skuId);
//        return itemService.getSkuInfoFromDbOrRedis(skuId);
//        return itemService.getSkuInfoFromDbOrRedisByRedis(skuId);
    }

    /**
     * 查询sku的图片信息
     * @param skuId
     * @return
     */
    @GmallCache(prefix = "image:")
    @GetMapping(value = "/getSkuImage/{skuId}")
    public List<SkuImage> getSkuImage(@PathVariable(value = "skuId") Long skuId){
        return itemService.getSkuImage(skuId);
    }

    /**
     * 获取sku的价格信息
     * @param skuId
     * @return
     */
    @GmallCache(prefix = "price:")
    @GetMapping(value = "/getPrice/{skuId}")
    public BigDecimal getPrice(@PathVariable(value = "skuId") Long skuId){
        return itemService.getPrice(skuId);
    }

    /**
     * 查询分类信息
     * @param category3Id
     * @return
     */
    @GmallCache(prefix = "category:")
    @GetMapping(value = "/getCategory/{category3Id}")
    public BaseCategoryView getCategory(@PathVariable(value = "category3Id") Long category3Id){
        return itemService.getCategory(category3Id);
    }

    /**
     * 根据skuid和spuid查询当前spu所有的销售属性和值的信息并且标注出当前sku的销售属性值
     * @param skuId
     * @param spuId
     * @return
     */
    @GmallCache(prefix = "saleAttrValue:")
    @GetMapping(value = "/getBySpuIdAndSkuId/{spuId}/{skuId}")
    public List<SpuSaleAttr> getBySpuIdAndSkuId(@PathVariable(value = "skuId") Long skuId,
                                                @PathVariable(value = "spuId") Long spuId){
        return itemService.getBySpuIdAndSkuId(spuId, skuId);
    }


    /**
     * 根据spuid查询该spu下所有sku的键值对
     * @param spuId
     * @return
     */
    @GmallCache(prefix = "saleAttrMap:")
    @GetMapping(value = "/getSkuSaleAttrBySpuId/{spuId}")
    public Map getSkuSaleAttrBySpuId(@PathVariable(value = "spuId") Long spuId){
        return itemService.getSkuSaleAttrBySpuId(spuId);
    }

    /**
     * 查询首页的分类信息
     * @return
     */
    @GmallCache(prefix = "indexCategory")
    @GetMapping(value = "/getIndexCategoryList")
    public List<JSONObject> getIndexCategoryList(){
        List<JSONObject> category = itemService.getCategory();
        return category;
    }

    /**
     * 通过品牌Id 集合来查询数据
     * @param tmId
     * @return
     */
    @GetMapping(value = "/getTrademark/{tmId}")
    public BaseTrademark getTrademark(@PathVariable(value = "tmId")Long tmId){
        BaseTrademark trademark = itemService.getTrademarkByTmId(tmId);
        return trademark;
    }

    /**
     * 通过SkuID查询集合数据
     * @param skuId
     * @return
     */
    @GetMapping(value = "/getAttrList/{skuId}")
    public List<BaseAttrInfo> getAttrList(@PathVariable(value = "skuId") Long skuId){
        List<BaseAttrInfo> attrList = itemService.getAttrList(skuId);
        return attrList;
    }

}
