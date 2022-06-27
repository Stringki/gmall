package com.atguigu.gmall.oauth.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.util.IpUtil;
import com.atguigu.gmall.oauth.service.LoginService;
import com.atguigu.gmall.oauth.util.AuthToken;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "/user")
public class LoginController {

    @Resource
    private LoginService loginService;

    @Resource
    private HttpServletRequest httpServletRequest;

    @Resource
    private StringRedisTemplate stringRedisTemplate;



    @PostMapping(value = "/login")
    public Result<AuthToken> login(String username, String password){
        AuthToken authToken = loginService.login(username, password);
        //获取用户的IP地址
        String ipAddress = IpUtil.getIpAddress(httpServletRequest);
        //把IP地址存入Redis
        stringRedisTemplate.boundValueOps("ip"+ ipAddress).set(authToken.getAccessToken());
        return Result.ok(authToken);
    }
}
