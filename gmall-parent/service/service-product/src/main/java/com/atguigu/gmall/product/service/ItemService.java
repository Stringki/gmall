package com.atguigu.gmall.product.service;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.model.product.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 商品详情微服务需要使用的相关service接口
 */
public interface ItemService {

    /**
     * 查询sku的详情的信息
     * @param skuId
     * @return
     */
     SkuInfo getSkuInfo(Long skuId);

    /**
     * 查询sku的详情的信息
     * @param skuId
     * @return
     */
     SkuInfo getSkuInfoFromDbOrRedis(Long skuId);

    /**
     * 查询sku的详情的信息
     * @param skuId
     * @return
     */
     SkuInfo getSkuInfoFromDbOrRedisByRedis(Long skuId);

    /**
     * 查询sku的图片信息
     * @param skuId
     * @return
     */
     List<SkuImage> getSkuImage(Long skuId);

    /**
     * 获取sku的价格信息
     * @param skuId
     * @return
     */
     BigDecimal getPrice(Long skuId);

    /**
     * 查询分类信息
     * @param category3Id
     * @return
     */
    BaseCategoryView getCategory(Long category3Id);

    /**
     * 根据skuid和spuid查询当前spu所有的销售属性和值的信息并且标注出当前sku的销售属性值
     * @param spuId
     * @param skuId
     * @return
     */
    List<SpuSaleAttr> getBySpuIdAndSkuId(Long spuId,
                                                Long skuId);
    /**
     * 根据spuid查询该spu下所有sku的键值对
     * @param spuId
     * @return
     */
    Map getSkuSaleAttrBySpuId(Long spuId);

    /**
     * 查询首页的分类信息
     * @return
     */
    List<JSONObject> getCategory();

    /**
     * 通过品牌ID查询数据
     * @param tmId
     * @return
     */
    BaseTrademark getTrademarkByTmId(Long tmId);

    /**
     * 通过skuId 集合来查询数据
     * @param skuId
     * @return
     */
    List<BaseAttrInfo> getAttrList(Long skuId);
}
