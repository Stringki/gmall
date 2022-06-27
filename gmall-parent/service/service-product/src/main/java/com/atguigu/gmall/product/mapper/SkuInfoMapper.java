package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.model.product.SkuInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * sku的核心表skuinfo的mapper映射
 */
@Mapper
public interface SkuInfoMapper extends BaseMapper<SkuInfo> {

}
