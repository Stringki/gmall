package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.model.product.BaseAttrValue;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 平台属性的值的mapper对象
 */
@Mapper
public interface BaseAttrValueMapper extends BaseMapper<BaseAttrValue> {
}
