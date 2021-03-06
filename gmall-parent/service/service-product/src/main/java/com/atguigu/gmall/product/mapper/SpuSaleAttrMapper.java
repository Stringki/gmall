package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * spu销售属性的名称表映射
 */
@Mapper
public interface SpuSaleAttrMapper extends BaseMapper<SpuSaleAttr> {

    /**
     * 根据spu的id查询该spu的所有的销售属性和销售属性的值
     * @param spuId
     * @return
     */
    public List<SpuSaleAttr> selectSpuSaleAttrBySpuId(@Param("spuId") Long spuId);

    /**
     * 根据skuid和spuid查询当前spu所有的销售属性和值的信息并且标注出当前sku的销售属性值
     * @param spuId
     * @param skuId
     * @return
     */
    public List<SpuSaleAttr> selectSpuSaleAttrBySpuIdAndSkuId(@Param("spuId") Long spuId,
                                                              @Param("skuId") Long skuId);
}
