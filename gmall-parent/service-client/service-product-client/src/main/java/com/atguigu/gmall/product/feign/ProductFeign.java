package com.atguigu.gmall.product.feign;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.model.product.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 商品管理微服务的feign接口
 */
@FeignClient(name = "service-product", path = "/admin/item")
public interface ProductFeign {

    /**
     * 查询sku的详情的信息
     * @param skuId
     * @return
     */
    @GetMapping(value = "/getSkuInfo/{skuId}")
    public SkuInfo getSkuInfo(@PathVariable(value = "skuId") Long skuId);

    /**
     * 查询sku的图片信息
     * @param skuId
     * @return
     */
    @GetMapping(value = "/getSkuImage/{skuId}")
    public List<SkuImage> getSkuImage(@PathVariable(value = "skuId") Long skuId);

    /**
     * 获取sku的价格信息
     * @param skuId
     * @return
     */
    @GetMapping(value = "/getPrice/{skuId}")
    public BigDecimal getPrice(@PathVariable(value = "skuId") Long skuId);

    /**
     * 查询分类信息
     * @param category3Id
     * @return
     */
    @GetMapping(value = "/getCategory/{category3Id}")
    public BaseCategoryView getCategory(@PathVariable(value = "category3Id") Long category3Id);

    /**
     * 根据skuid和spuid查询当前spu所有的销售属性和值的信息并且标注出当前sku的销售属性值
     * @param skuId
     * @param spuId
     * @return
     */
    @GetMapping(value = "/getBySpuIdAndSkuId/{spuId}/{skuId}")
    public List<SpuSaleAttr> getBySpuIdAndSkuId(@PathVariable(value = "skuId") Long skuId,
                                                @PathVariable(value = "spuId") Long spuId);

    /**
     * 根据spuid查询该spu下所有sku的键值对
     * @param spuId
     * @return
     */
    @GetMapping(value = "/getSkuSaleAttrBySpuId/{spuId}")
    public Map getSkuSaleAttrBySpuId(@PathVariable(value = "spuId") Long spuId);


    /**
     * 查询首页的分类信息
     * @return
     */
    @GetMapping(value = "/getIndexCategoryList")
    public List<JSONObject> getIndexCategoryList();

    /**
     * 通过品牌Id 集合来查询数据
     * @param tmId
     * @return
     */
    @GetMapping("/getTrademark/{tmId}")
    BaseTrademark getTrademark(@PathVariable("tmId")Long tmId);

    /**
     * 通过skuId 集合来查询数据
     * @param skuId
     * @return
     */
    @GetMapping("/getAttrList/{skuId}")
    List<BaseAttrInfo> getAttrList(@PathVariable("skuId") Long skuId);
}
