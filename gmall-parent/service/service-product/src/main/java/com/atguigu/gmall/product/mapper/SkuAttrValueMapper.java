package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.model.product.SkuAttrValue;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * sku的平台属性值表的mapper映射
 */
@Mapper
public interface SkuAttrValueMapper extends BaseMapper<SkuAttrValue> {
}
