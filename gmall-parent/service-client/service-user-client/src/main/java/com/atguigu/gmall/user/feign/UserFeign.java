package com.atguigu.gmall.user.feign;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.user.UserAddress;
import com.atguigu.gmall.model.user.UserInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "service-user", path = "/api/user")
public interface UserFeign {

    /**
     * 查询用户信息
     * @return
     */
    @GetMapping(value = "/getUserInfo/{username}")
    UserInfo getUserInfo(@PathVariable(value = "username") String username);

    /**
     * 根据用户ID查询收货地址
     * @param userId
     * @return
     */
    @GetMapping(value = "/getUserAddress/{userId}")
    public Result<List<UserAddress>> getUserAddress(@PathVariable(value = "userId") Long userId);

}
