package com.atguigu.gmall.user.service;

import com.atguigu.gmall.model.user.UserAddress;

import java.util.List;

public interface UserAddressService {

    /**
     * 根据用户ID查询用户的收货地址
     * @param userId
     * @return
     */
    List<UserAddress> findUserAddress(Long userId);

}
