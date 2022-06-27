package com.atguigu.gmall.user.service.impl;

import com.atguigu.gmall.model.user.UserAddress;
import com.atguigu.gmall.user.mapper.UserAddressMapper;
import com.atguigu.gmall.user.service.UserAddressService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class UserAddressServiceImpl implements UserAddressService {

    @Resource
    private UserAddressMapper userAddressMapper;

    /**
     *根据用户ID查询用户收货地址
     * @param userId
     * @return
     */
    @Override
    public List<UserAddress> findUserAddress(Long userId) {
        //参数校验
        if (userId == null){
            return null;
        }
        //根据userId查询用户收货地址（多个地址列表）
        List<UserAddress> userAddressList =
                userAddressMapper.selectList(new LambdaQueryWrapper<UserAddress>().eq(UserAddress::getUserId, userId));
        return userAddressList;
    }
}
