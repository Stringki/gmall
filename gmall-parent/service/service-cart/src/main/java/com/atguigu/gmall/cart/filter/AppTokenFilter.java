package com.atguigu.gmall.cart.filter;

import com.atguigu.gmall.cart.util.GmallThreadLocalUtils;
import com.atguigu.gmall.cart.util.TokenUtil;
import com.atguigu.gmall.model.user.UserInfo;
import com.atguigu.gmall.user.feign.UserFeign;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.GenericFilterBean;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;
import java.util.Map;

/**
 * 过滤器
 */
@Order(1)//过滤器的执行顺序
@WebFilter(filterName = "appTokenFilter", urlPatterns = "/*")
public class AppTokenFilter extends GenericFilterBean {

    @Resource
    private UserFeign userFeign;

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        //从令牌中获取用户的基本信息
        Map<String, String> userInfo = TokenUtil.getUserInfo();
        //判断是否为空
        if(userInfo != null && userInfo.size() > 0){
            //将用户的名字存入本地线程
            UserInfo feignUserInfo = userFeign.getUserInfo(userInfo.get("username"));
            String username = String.valueOf(feignUserInfo.getId());
            GmallThreadLocalUtils.setUserName(username);
        }
        chain.doFilter(req,res);
    }
}