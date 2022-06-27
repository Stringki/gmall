package com.atguigu.gmall.user.service;

import com.atguigu.gmall.model.user.UserInfo;

public interface UserInfoService {

    /**
     * 根据username查询用户信息
     * @param username
     * @return
     */
    UserInfo getUserInfo(String username);

}
