package com.atguigu.gmall.gateway.filter;

import com.atguigu.gmall.common.util.IpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class GmallGlobalFilter implements GlobalFilter, Ordered {
    /**
     * 全局过滤逻辑
     * @param exchange
     * @param chain
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        //获取用户的请求地址
        String path = request.getURI().getPath().toString();
        //登录地址放行
        if (path.contains("/login")){
            return chain.filter(exchange);
        }
        //获取用户令牌信息---url中
        String token = request.getQueryParams().getFirst("token");
        if (StringUtils.isEmpty(token)){
            //head中获取
            token = request.getHeaders().getFirst("token");
            if (StringUtils.isEmpty(token)){
                MultiValueMap<String, HttpCookie> cookies = request.getCookies();
                if (cookies == null || cookies.size()<=0){
                    //401,未授权,无法访问
                    response.setStatusCode(HttpStatus.UNAUTHORIZED);
                    //拒绝请求
                    return response.setComplete();
                }
                token = request.getCookies().getFirst("token").getValue();
            }
        }
        //校验令牌是否盗用
        if (!checkIpAddress(request,token)){
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }
        request.mutate().header("Authorization","Bearer " + token);
        //放行
        return chain.filter(exchange);
    }

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    /**
     * 校验令牌和ip是否匹配
     * @return
     */
    private boolean checkIpAddress(ServerHttpRequest request, String token) {
        //获取ip地址
        String gatwayIpAddress = IpUtil.getGatwayIpAddress(request);
        //获取redis中的token信息
        String oldToken = stringRedisTemplate.boundValueOps("ip" + gatwayIpAddress).get();
        //判断是否一致,不一致则为盗用
        if (token == null || !token.equals(oldToken)){
            return false;
        }
        return true;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
