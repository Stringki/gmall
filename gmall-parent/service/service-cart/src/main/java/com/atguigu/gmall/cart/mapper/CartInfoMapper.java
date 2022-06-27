package com.atguigu.gmall.cart.mapper;

import com.atguigu.gmall.model.cart.CartInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 购物车Mapper层
 */
@Mapper
public interface CartInfoMapper extends BaseMapper<CartInfo> {
}
