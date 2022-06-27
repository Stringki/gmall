package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.model.product.SkuSaleAttrValue;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * sku的销售属性值表的mapper映射
 */
@Mapper
public interface SkuSaleAttrValueMapper extends BaseMapper<SkuSaleAttrValue> {

    /**
     * 根据spuid查询该spu下所有sku的键值对
     * @param spuId
     * @return
     */
    public List<Map> selectSkuSaleAttrBySpuId(@Param("spuId") Long spuId);
}
