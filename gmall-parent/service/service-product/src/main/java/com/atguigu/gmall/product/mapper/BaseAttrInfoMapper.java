package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 平台属性的名称的mapper对象
 */
@Mapper
public interface BaseAttrInfoMapper extends BaseMapper<BaseAttrInfo> {

    /**
     * 根据分类查询平台属性列表
     * @param category1Id
     * @param category2Id
     * @param category3Id
     * @return
     */
    public List<BaseAttrInfo> selectBaseAttrInfoListByCategory(@Param("category1Id") Long category1Id,
                                                               @Param("category2Id") Long category2Id,
                                                               @Param("category3Id") Long category3Id);

    /**
     *查询Sku对应平台属性
     * @param skuId
     */
    List<BaseAttrInfo> selectBaseAttrInfoListBySkuId(@Param("skuId")Long skuId);

}
