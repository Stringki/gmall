package com.atguigu.gmall.oauth.service.impl;

import com.atguigu.gmall.oauth.service.LoginService;
import com.atguigu.gmall.oauth.util.AuthToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.Map;

@Service
public class LoginServiceImpl implements LoginService {

    @Resource
    private LoadBalancerClient loadBalancerClient;

    @Resource
    private RestTemplate restTemplate;

    @Override
    public AuthToken login(String username, String password) {
        //检验参数
        if (username == null || password == null){
            throw new  RuntimeException();
        }
        //获取服务实体
        ServiceInstance instance = loadBalancerClient.choose("service-oauth");
        //获取请求地址
        String url = instance.getUri()+"/oauth/token";
        //定义请求头
        MultiValueMap<String,String> headers = new HttpHeaders();
        headers.add("Authorization",getHeadInfo());
        //定义请求Body
        MultiValueMap<String,String> body = new HttpHeaders();
        body.add("grant_type","password");
        body.add("username",username);
        body.add("password",password);
        //定义请求对象
        HttpEntity objectHttpEntity = new HttpEntity<>(body, headers);
        //发起请求
        ResponseEntity<Map> exchange =
                restTemplate.exchange(url, HttpMethod.POST, objectHttpEntity, Map.class);
        //解析结果
        Map result = exchange.getBody();
        if(result == null || result.size() <= 0){
            return null;
        }
        //获取令牌信息
        AuthToken authToken = new AuthToken();
        authToken.setAccessToken((String)result.get("access_token"));
        authToken.setJti((String)result.get("jti"));
        authToken.setRefreshToken((String)result.get("refresh_token"));
        //返回结果
        return authToken;
    }

    @Value("${auth.clientId}")
    private String clientId;
    @Value("${auth.clientSecret}")
    private String clientSecret;

    /**
     * 获取请求头拼接参数
     * @return
     */
    private String getHeadInfo() {
        try {
            String param = clientId + ":" + clientSecret;
            byte[] encode = Base64Utils.encode(param.getBytes());
            return "Basic " + new String(encode, "UTF-8");
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
