package com.atguigu.gmall.cart.controller;

import com.atguigu.gmall.cart.service.CartInfoService;
import com.atguigu.gmall.cart.util.GmallThreadLocalUtils;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.util.AuthContextHolder;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.user.UserInfo;
import com.atguigu.gmall.user.feign.UserFeign;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping(value = "/api/cart")
public class CarInfoController {

    @Resource
    private CartInfoService cartInfoService;
    @Resource
    private UserFeign userFeign;

    /**
     * 新增购物车
     * @param skuId
     * @param num
     * @return
     */
    @GetMapping(value = "/CrudCartInfo")
    public Result<CartInfo> CrudCartInfo(Long skuId,Integer num){
        String userName = GmallThreadLocalUtils.getUserName();
        CartInfo cartInfo = cartInfoService.CartInfoCrud(skuId, userName, num);
        return Result.ok(cartInfo);
    }

    /**
     * 合并购物车
     * @param cartInfoList
     * @return
     */
    @PostMapping(value = "/mergeCartInfo")
    public Result mergeCartInfo(@RequestBody List<CartInfo> cartInfoList){
        String userName = GmallThreadLocalUtils.getUserName();
        cartInfoService.mergeCartInfo(cartInfoList,userName);
        return Result.ok();
    }

    /**
     * 删除用户购物车
     * @param skuId
     * @param request
     * @return
     */
    @DeleteMapping(value = "/delCart/{skuId}")
    public Result delCart(@PathVariable("skuId") Long skuId, HttpServletRequest request){
        String userName = GmallThreadLocalUtils.getUserName();
        if (StringUtils.isEmpty(userName)){
            userName = AuthContextHolder.getUserTempId(request);
        }
        cartInfoService.delCart(skuId,userName);
        return Result.ok();
    }


    /**
     * 查询购物车
     * @return
     */
    @GetMapping(value = "/getCartInfo")
    public Result<List<CartInfo>> getCartInfo(){
        String userName = GmallThreadLocalUtils.getUserName();
        List<CartInfo> cartInfo = cartInfoService.getCartInfo(userName);
        return Result.ok(cartInfo);
    }

    /**
     * 查询用户当前需要购买的商品的列表
     * @return
     */
    @GetMapping(value = "/getCartList")
    public List<CartInfo> getCartList(){
        String userName = GmallThreadLocalUtils.getUserName();
        List<CartInfo> cartInfoList = cartInfoService.getCartList(userName);
        return cartInfoList;
    }

    /**
     * Feign调用获取用户Id查询购物车信息
     * @param username
     * @return
     */
    public String getUserName(String username){
        UserInfo userInfo = userFeign.getUserInfo(username);
        return String.valueOf(userInfo.getId());
    }

}
