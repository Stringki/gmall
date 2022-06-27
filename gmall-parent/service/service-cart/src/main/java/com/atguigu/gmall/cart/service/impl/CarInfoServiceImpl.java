package com.atguigu.gmall.cart.service.impl;

import com.atguigu.gmall.cart.mapper.CartInfoMapper;
import com.atguigu.gmall.cart.service.CartInfoService;
import com.atguigu.gmall.cart.service.AsyncCartInfoService;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.product.feign.ProductFeign;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Transactional(rollbackFor = Exception.class)
@Service
public class CarInfoServiceImpl implements CartInfoService {

    @Resource
    private CartInfoMapper cartInfoMapper;
    @Resource
    private ProductFeign productFeign;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private AsyncCartInfoService asyncCartInfoService;

    /**
     * 购物车的操作
     * @param skuId
     * @param userId
     * @param num
     * @return
     */
    @Override
    public CartInfo CartInfoCrud(Long skuId, String userId, Integer num) {
        //检验参数
        if (skuId == null || num == null || StringUtils.isEmpty(userId)){
            throw new RuntimeException("参数错误！！！");
        }
        //判断num是否为负数
        if (num <= 0){
            //小于0就是清空购物车操作
            delCartInfo(skuId,userId);
            return new CartInfo();
        }
        //查询当前商品的购物车信息
        LambdaQueryWrapper<CartInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CartInfo::getSkuId,skuId).eq(CartInfo::getUserId,userId);
        CartInfo cartInfo = cartInfoMapper.selectOne(wrapper);
        //判断当前商品是否加入过购物车--未添加
        if (cartInfo == null || cartInfo.getId() == null){
            //如果全为空进入增加
            //查询商品的信息
            SkuInfo skuInfo = productFeign.getSkuInfo(skuId);
            //检验商品信息
            if (skuInfo == null || skuInfo.getId() == null){
                //抛出参数异常
                throw new RuntimeException("商品信息不存在  无法加入购物车！！！");
            }
            //把商品信息包装成购物车对象
            cartInfo = new CartInfo();
            cartInfo.setUserId(userId);
            cartInfo.setSkuId(skuId);
            cartInfo.setCartPrice(skuInfo.getPrice());//商品的实际数据库价格
            cartInfo.setSkuNum(num);
            cartInfo.setImgUrl(skuInfo.getSkuDefaultImg());//商品默认图片
            cartInfo.setSkuName(skuInfo.getSkuName());
            //把数据添加到数据库中
            asyncCartInfoService.addCart(cartInfo);
        }else {
            //如果已经添加过购物车
            if ((cartInfo.getSkuNum() +num) <=0){
                    delCartInfo(skuId,userId);
                    return new CartInfo();
            }
            //判断合并后的数量是否小于0 -- 删除
            cartInfo.setSkuNum(cartInfo.getSkuNum()+num);
            //更新数据库数据
            asyncCartInfoService.updateCart(cartInfo);
        }
        //更新redis中的数据
        redisTemplate.boundHashOps("cart:" + userId + ":info").put(skuId + "", cartInfo);
        //返回结果
        return cartInfo;
    }

    /**
     * 新增购物车
     * @param skuId
     * @param userId
     * @param num
     * @return
     */
    @Async
    @Override
    public CartInfo addCart(Long skuId, String userId, Integer num) {
        //参数校验
        if(skuId == null || num == null || StringUtils.isEmpty(userId) || num <= 0){
            throw new RuntimeException("参数错误!!!");
        }
        //查询商品的信息
        SkuInfo skuInfo = productFeign.getSkuInfo(skuId);
        //判断商品是否为空
        if(skuInfo == null || skuInfo.getId() == null){
            throw new RuntimeException("商品不存在,无法加入购物车!!!");
        }
        CartInfo cartInfo = new CartInfo();
        //将商品的信息包装为购物车对象
        cartInfo.setUserId(userId);
        cartInfo.setSkuId(skuId);
        cartInfo.setCartPrice(skuInfo.getPrice());
        cartInfo.setSkuNum(num);
        cartInfo.setImgUrl(skuInfo.getSkuDefaultImg());
        cartInfo.setSkuName(skuInfo.getSkuName());
        //将购物车对象添加到数据库中区
        asyncCartInfoService.addCart(cartInfo);
        //更新redis中的数据
        redisTemplate.boundHashOps("cart:" + userId + ":info").put(skuId + "", cartInfo);
        return cartInfo;
    }

    /**
     * 清空购物车删除加入购物车的数据
     * @param skuId
     * @param userId
     */
    private void delCartInfo(Long skuId, String userId) {
        //检验参数
        if (skuId == null || StringUtils.isEmpty(userId)){
            return;
        }
        LambdaQueryWrapper<CartInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CartInfo::getSkuId,skuId)
                .eq(CartInfo::getUserId,userId);
        cartInfoMapper.delete(wrapper);
        //从Redis中删除购物车数据
        redisTemplate.boundHashOps("cart:" + userId + ":info").delete(skuId+"");
    }


    /**
     * 合并购物车
     * @param cartInfoList
     * @param userId
     */
    @Override
    public void mergeCartInfo(List<CartInfo> cartInfoList, String userId) {
        //参数校验
        if (cartInfoList == null || StringUtils.isEmpty(userId) || cartInfoList.size()==0){
            return;
        }
        //从数据库中查询用户登陆是否有加入购物车
        for (CartInfo cartInfo : cartInfoList){
            //从数据库查询购物车的信息
            LambdaQueryWrapper<CartInfo> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(CartInfo::getUserId,userId)
                            .eq(CartInfo::getSkuId,cartInfo.getSkuId());
            CartInfo info = cartInfoMapper.selectOne(wrapper);
            //判断是否存在
            if (info!=null && info.getId()!=null){
                //把数据库中的数量与当前累加
                info.setSkuNum(info.getSkuNum()+cartInfo.getSkuNum());
                //异步更新数据
                asyncCartInfoService.updateCart(info);
                //redis更新
                redisTemplate.boundHashOps("cart:"+userId+":info").put(info.getUserId()+"",info);
            }else {
                //若不存在直接添加
                addCart(cartInfo.getSkuId(),cartInfo.getUserId(),cartInfo.getSkuNum());
            }
        }
    }

    /**
     * 删除用户购物车
     * @param skuId
     * @param userId
     */
    @Override
    public void delCart(Long skuId, String userId) {
        //校验参数
        if (skuId == null || StringUtils.isEmpty(userId)){
            throw new RuntimeException("参数错误");
        }
        delCartInfo(skuId,userId);
    }

    /**
     * 查询购物车信息
     * @param userId
     * @return
     */
    @Override
    public List<CartInfo> getCartInfo(String userId) {
        //判断参数
        if(userId == null){
            return null;
        }
        //从Redis中查询数据
        List cartList = redisTemplate.boundHashOps("cart:" + userId + ":info").values();
        //判断redis中是否有该数据
        if (cartList != null && cartList.size()>0){
            return cartList;
        }
        //没有则查数据库
        LambdaQueryWrapper<CartInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CartInfo::getUserId,userId);
        List<CartInfo> cartInfoList = cartInfoMapper.selectList(wrapper);
        if (cartInfoList !=null && cartInfoList.size()>0){
            //数据库中有数据则存入Redis
            for (CartInfo cartInfo : cartInfoList){
                redisTemplate.boundHashOps("cart:" + userId + ":info").put(cartInfo.getSkuId() + "", cartInfo);
            }
        }else {
            //如果数据库中也没有则缓存空对象到redis
            redisTemplate.boundHashOps("cart:"+ userId + ":info").expire(300L, TimeUnit.SECONDS);//设置销毁时间
            redisTemplate.boundHashOps("cart:"+ userId + ":info").put("null","null");
        }
        return cartInfoList;
    }

    /**
     * 查询用户当前需要购买的商品的列表-Feign
     * @param username
     * @return
     */
    @Override
    public List<CartInfo> getCartList(String username) {

        List<CartInfo> cartInfoList = cartInfoMapper.selectList(
                new LambdaQueryWrapper<CartInfo>()
                        .eq(CartInfo::getUserId, username)
                        .eq(CartInfo::getIsChecked, 1));
        return cartInfoList;
    }
}
