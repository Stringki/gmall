package com.atguigu.gmall.user.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.user.UserAddress;
import com.atguigu.gmall.user.service.UserAddressService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping(value = "/api/user")
public class UserAddressController {

    @Resource
    private UserAddressService userAddressService;

    /**
     * 根据用户ID查询收货地址
     * @param userId
     * @return
     */
    @GetMapping(value = "/getUserAddress/{userId}")
    public Result<List<UserAddress>> getUserAddress(@PathVariable(value = "userId") Long userId){
        List<UserAddress> addressList = userAddressService.findUserAddress(userId);
        return Result.ok(addressList);
    }
}
