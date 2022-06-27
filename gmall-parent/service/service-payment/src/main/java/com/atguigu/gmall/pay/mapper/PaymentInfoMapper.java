package com.atguigu.gmall.pay.mapper;

import com.atguigu.gmall.model.payment.PaymentInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 支付记录的Mapper
 */
@Mapper
public interface PaymentInfoMapper extends BaseMapper<PaymentInfo> {
}
