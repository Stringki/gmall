package com.atguigu.gmall.order.controller;

import com.atguigu.gmall.cart.feign.CartFeign;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.util.AuthContextHolder;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.order.OrderDetail;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.user.UserAddress;
import com.atguigu.gmall.order.service.OrderService;
import com.atguigu.gmall.order.util.GmallThreadLocalUtils;
import com.atguigu.gmall.product.feign.ProductFeign;
import com.atguigu.gmall.user.feign.UserFeign;
import javafx.scene.transform.Scale;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/order")
public class OrderApiController {

    @Resource
    private UserFeign userFeign;
    @Resource
    private ProductFeign productFeign;
    @Resource
    private CartFeign cartFeign;

    /**
     * 通过用户ID查询购物车的订单详情
     * @param request
     * @return
     */
    @GetMapping(value = "/auth/trade")
    public Result<Map<String,Object>> trade(HttpServletRequest request){

        //初始化Map
        Map<String, Object> map = new HashMap<>();
        //获取当前用户登陆的ID
        //String userId = AuthContextHolder.getUserId(request);
        //Long userAddressId = new Long(userId);
        String username = GmallThreadLocalUtils.getMap().get("username");
        Long userAddressId = new Long(username);
        //查询用户收货地址
        Result<List<UserAddress>> userAddress = userFeign.getUserAddress(userAddressId);
        //查询用户选中购物车中商品的信息
        List<CartInfo> cartList = cartFeign.getCartList();
        List<CartInfo> cartInfoList = cartList.stream().filter(car ->
                car.getIsChecked() == 1).collect(Collectors.toList());
        //计算商品总价
        BigDecimal totalPrice = new BigDecimal(0.00);//初始化价格
        Integer totalNum = 0;//初始化总台数
        List<OrderDetail> details = new ArrayList<>();
        for (CartInfo cartInfo : cartInfoList){
            //价格累加
            BigDecimal cartPrice = productFeign.getPrice(cartInfo.getSkuId());//数据库的手机价格
            totalPrice = totalPrice.add(cartPrice);
            //数量累加
            totalNum += cartInfo.getSkuNum();
            //初始化订单详情
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setSkuId(cartInfo.getSkuId());
            orderDetail.setSkuName(cartInfo.getSkuName());
            orderDetail.setImgUrl(cartInfo.getImgUrl());
            orderDetail.setOrderPrice(cartPrice);//获取数据库手机价格
            orderDetail.setSkuNum(cartInfo.getSkuNum());
            details.add(orderDetail);
        }
        // 获取流水号
        String tradeNo = orderService.getTradeNo(username);
        map.put("tradeNo", tradeNo);
        //返回结果
        map.put("totalPrice", totalPrice);
        map.put("totalNum", totalNum);
        map.put("addresses", userAddress);
        map.put("details", details);
        //返回
        return Result.ok(map);
    }

    @Resource
    private OrderService orderService;
    /**
     * 新增订单
     * @return
     */
    @PostMapping(value = "/auth/submitOrder")
    public Result submitOrder(@RequestBody OrderInfo orderInfo, HttpServletRequest request){
        //获取登陆用户的ID
        String userId = AuthContextHolder.getUserId(request);
        orderInfo.setUserId(Long.parseLong(userId));
        //验证流水号
        String tradeNo = request.getHeader("tradeNo");
        boolean code = orderService.checkTradeCode(userId, tradeNo);
        if (!code){
            return Result.fail("重复订单提交!");
        }
        //删除流水号
        orderService.deleteTradeNo(userId);
        //验证通过保存订单
        Long orderInId = orderService.saveOrderInfo(orderInfo);
        return Result.ok(orderInId);
    }

    /**
     * 根据订单ID查询订单详情
     * @param orderId
     * @return
     */
    @GetMapping(value = "/getOrDeInfo/{orderId}")
    public OrderInfo getOrDeInfo(@PathVariable(value = "orderId") Long orderId){
        OrderInfo orDeInfo = orderService.getOrDeInfo(orderId);
        return orDeInfo;
    }

}
