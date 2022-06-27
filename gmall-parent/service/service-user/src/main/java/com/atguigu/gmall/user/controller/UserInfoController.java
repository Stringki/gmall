package com.atguigu.gmall.user.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.user.UserInfo;
import com.atguigu.gmall.user.service.UserInfoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping(value = "/api/user")
public class UserInfoController {

    @Resource
    private UserInfoService userInfoService;

    /**
     * 根据用户名查询数据
     * @param username
     * @return
     */
    @GetMapping(value = "/getUserInfo/{username}")
    public UserInfo getUserInfo(@PathVariable(value = "username")String username){
        UserInfo userInfo = userInfoService.getUserInfo(username);
        return userInfo;
    }

}
