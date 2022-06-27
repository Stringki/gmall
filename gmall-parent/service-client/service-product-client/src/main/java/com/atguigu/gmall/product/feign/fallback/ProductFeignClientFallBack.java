package com.atguigu.gmall.product.feign.fallback;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.feign.ProductFeign;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 降级类
 */
@Component
public class ProductFeignClientFallBack implements ProductFeign {
    @Override
    public SkuInfo getSkuInfo(Long skuId) {
        return null;
    }

    @Override
    public List<SkuImage> getSkuImage(Long skuId) {
        return null;
    }

    @Override
    public BigDecimal getPrice(Long skuId) {
        return null;
    }

    @Override
    public BaseCategoryView getCategory(Long category3Id) {
        return null;
    }

    @Override
    public List<SpuSaleAttr> getBySpuIdAndSkuId(Long skuId, Long spuId) {
        return null;
    }

    @Override
    public Map getSkuSaleAttrBySpuId(Long spuId) {
        return null;
    }

    @Override
    public List<JSONObject> getIndexCategoryList() {
        return null;
    }

    @Override
    public BaseTrademark getTrademark(Long tmId) {
        return null;
    }

    @Override
    public List<BaseAttrInfo> getAttrList(Long skuId) {
        return null;
    }
}
