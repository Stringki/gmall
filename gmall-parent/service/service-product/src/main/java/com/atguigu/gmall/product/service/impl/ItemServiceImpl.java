package com.atguigu.gmall.product.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.mapper.*;
import com.atguigu.gmall.product.service.ItemService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.log4j.Log4j2;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 商品详情微服务需要使用的相关service接口的实现类
 */
@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private SkuInfoMapper skuInfoMapper;
    /**
     * 查询sku的详情的信息
     *
     * @param skuId
     * @return
     */
    @Override
    public SkuInfo getSkuInfo(Long skuId) {
        return skuInfoMapper.selectById(skuId);
    }

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;
    /**
     * 查询sku的详情的信息
     *
     * @param skuId
     * @return
     */
    @Override
    public SkuInfo getSkuInfoFromDbOrRedis(Long skuId) {
        //参数校验
        if(skuId == null){
            return null;
        }
        //定义key--->sku:1:info
        String key = RedisConst.SKUKEY_PREFIX + skuId + RedisConst.SKUKEY_SUFFIX;
        //从redis中获取值
        Object o = redisTemplate.boundValueOps(key).get();
        //redis中有值,直接返回
        if(o != null){
            return (SkuInfo) o;
        }
        //若redis中没有值,从数据库中获取值---> sku:1:lock
        String lockKey = RedisConst.SKUKEY_PREFIX + skuId + RedisConst.SKULOCK_SUFFIX;
        RLock lock = redissonClient.getLock(lockKey);
        try {
            if(lock.tryLock(RedisConst.SKULOCK_EXPIRE_PX1,
                    RedisConst.SKULOCK_EXPIRE_PX2,
                    TimeUnit.SECONDS)){
                //从数据库中获取数据
                SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
                //判断数据库中是否为空
                if(skuInfo == null || skuInfo.getId() == null){
                    //防止缓存穿透:设置过期时间为2分钟
                    redisTemplate.boundValueOps(key).set(skuInfo,
                            RedisConst.SKUKEY_ISNULL_TIMEOUT,
                            TimeUnit.SECONDS);
                }else{
                    //将数据缓存到redis中去
                    redisTemplate.boundValueOps(key).set(skuInfo,
                            RedisConst.SKUKEY_TIMEOUT,
                            TimeUnit.SECONDS);
                }
                //返回数据
                return skuInfo;
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            //释放锁
            lock.unlock();
        }
        return null;
    }

    /**
     * 查询sku的详情的信息
     *
     * @param skuId
     * @return
     */
    @Override
    public SkuInfo getSkuInfoFromDbOrRedisByRedis(Long skuId) {
        //参数校验
        if(skuId == null){
            return null;
        }
        //定义key--->sku:1:info
        String key = RedisConst.SKUKEY_PREFIX + skuId + RedisConst.SKUKEY_SUFFIX;
        //从redis中获取值
        Object o = redisTemplate.boundValueOps(key).get();
        //redis中有值,直接返回
        if(o != null){
            return (SkuInfo) o;
        }
        try {
            //若redis中没有值,从数据库中获取值---> sku:1:lock
            String uuid = UUID.randomUUID().toString().replace("-", "");
            String lockKey = RedisConst.SKUKEY_PREFIX + skuId + RedisConst.SKULOCK_SUFFIX;
            Boolean aBoolean =
                    redisTemplate.boundValueOps(lockKey).setIfAbsent(uuid,
                            RedisConst.SKULOCK_EXPIRE_PX2,
                            TimeUnit.SECONDS);
            //判断加锁是否成功
            if(aBoolean){
                //从数据库中获取数据
                SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
                //判断数据库中是否为空
                if(skuInfo == null || skuInfo.getId() == null){
                    //防止缓存穿透:设置过期时间为2分钟
                    redisTemplate.boundValueOps(key).set(skuInfo,
                            RedisConst.SKUKEY_ISNULL_TIMEOUT,
                            TimeUnit.SECONDS);
                }else{
                    //将数据缓存到redis中去
                    redisTemplate.boundValueOps(key).set(skuInfo,
                            RedisConst.SKUKEY_TIMEOUT,
                            TimeUnit.SECONDS);
                }
                //释放锁
                DefaultRedisScript script = new DefaultRedisScript();
                script.setScriptText("if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end");
                script.setResultType(Long.class);
                redisTemplate.execute(script, Arrays.asList(lockKey), uuid);
                //返回数据
                return skuInfo;
            }else{
                //休眠
                Thread.sleep(RedisConst.SKUKEY_SLEEP);
                //递归
                getSkuInfoFromDbOrRedisByRedis(skuId);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Autowired
    private SkuImageMapper skuImageMapper;
    /**
     * 查询sku的图片信息
     *
     * @param skuId
     * @return
     */
    @Override
    public List<SkuImage> getSkuImage(Long skuId) {
        return skuImageMapper.selectList(new LambdaQueryWrapper<SkuImage>().eq(SkuImage::getSkuId, skuId));
    }

    /**
     * 获取sku的价格信息
     *
     * @param skuId
     * @return
     */
    @Override
    public BigDecimal getPrice(Long skuId) {
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        return skuInfo.getPrice();
    }

    @Autowired
    private BaseCategoryViewMapper baseCategoryViewMapper;
    /**
     * 查询分类信息
     *
     * @param category3Id
     * @return
     */
    @Override
    public BaseCategoryView getCategory(Long category3Id) {
        return baseCategoryViewMapper
                .selectOne(new LambdaQueryWrapper<BaseCategoryView>()
                                .eq(BaseCategoryView::getCategory3Id, category3Id));
    }

    @Autowired
    private SpuSaleAttrMapper spuSaleAttrMapper;

    /**
     * 根据skuid和spuid查询当前spu所有的销售属性和值的信息并且标注出当前sku的销售属性值
     * @param spuId
     * @param skuId
     * @return
     */
    public List<SpuSaleAttr> getBySpuIdAndSkuId(Long spuId,
                                                Long skuId){
        return spuSaleAttrMapper.selectSpuSaleAttrBySpuIdAndSkuId(spuId,skuId);
    }

    @Autowired
    private SkuSaleAttrValueMapper skuSaleAttrValueMapper;
    /**
     * 根据spuid查询该spu下所有sku的键值对
     *
     * @param spuId
     * @return
     */
    @Override
    public Map getSkuSaleAttrBySpuId(Long spuId) {
        Map result = new HashMap<>();
        //获取全部的键值对
        List<Map> maps = skuSaleAttrValueMapper.selectSkuSaleAttrBySpuId(spuId);
        //遍历获取结果
        for (Map map : maps) {
            result.put(map.get("ids"), map.get("sku_id"));
        }
        return result;
    }

    /**
     * 查询首页的分类信息
     *
     * @return
     */
    @Override
    public List<JSONObject> getCategory() {
        //查询所有的分类信息
        List<BaseCategoryView> categoryViewList = baseCategoryViewMapper.selectList(null);
        //解析一级分类
        Map<Long, List<BaseCategoryView>> category1Map =
                categoryViewList.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory1Id));
        //针对一级分类进行遍历解析
        List<JSONObject> category1List = new ArrayList<>();
        for (Map.Entry<Long, List<BaseCategoryView>> category1 : category1Map.entrySet()) {
            JSONObject category1Json = new JSONObject();
            //获取一级分类的id
            Long cagegory1Id = category1.getKey();
            category1Json.put("categoryId",cagegory1Id);
            //一级分类的子分类信息
            List<BaseCategoryView> category2List= category1.getValue();
            if(category2List.size() > 0){
                category1Json.put("categoryName", category2List.get(0).getCategory1Name());
            }
            //进行groupby
            Map<Long, List<BaseCategoryView>> category2Map =
                    category2List.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory2Id));
            //遍历解析三级分类
            List<JSONObject> category2JsonList = new ArrayList<>();
            for (Map.Entry<Long, List<BaseCategoryView>> category2 : category2Map.entrySet()) {
                JSONObject category2Json = new JSONObject();
                //获取二级分类的ID
                Long category2Id = category2.getKey();
                //对应的三级分类获取
                List<BaseCategoryView> categoryViewList3 = category2.getValue();
                category2Json.put("categoryId", category2Id);
                if(categoryViewList3.size() > 0){
                    category2Json.put("categoryName",categoryViewList3.get(0).getCategory2Name());
                }
                List<JSONObject> category3JsonList = categoryViewList3.stream().map(category3 -> {
                    JSONObject category3Json = new JSONObject();
                    category3Json.put("categoryId", category3.getCategory3Id());
                    category3Json.put("categoryName", category3.getCategory3Name());
                    return category3Json;
                }).collect(Collectors.toList());
                category2Json.put("categoryChild", category3JsonList);
                category2JsonList.add(category2Json);
            }
            category1Json.put("categoryChild", category2JsonList);
            category1List.add(category1Json);
        }
        //返回数据
        return category1List;
    }

    @Resource
    private BaseTradeMarkMapper baseTradeMarkMapper;

    /**
     * 通过品牌ID查询数据
     * @param tmId
     * @return
     */
    @Override
    public BaseTrademark getTrademarkByTmId(Long tmId) {
        BaseTrademark baseTrademark = baseTradeMarkMapper.selectById(tmId);
        return baseTrademark;
    }

    @Resource
    private BaseAttrInfoMapper baseAttrInfoMapper;

    /**
     * 通过skuId 集合来查询数据
     * @param skuId
     * @return
     */
    @Override
    public List<BaseAttrInfo> getAttrList(Long skuId) {
        List<BaseAttrInfo> attrInfoList = baseAttrInfoMapper.selectBaseAttrInfoListBySkuId(skuId);
        return attrInfoList;
    }
}
