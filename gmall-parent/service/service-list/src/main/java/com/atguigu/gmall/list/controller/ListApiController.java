package com.atguigu.gmall.list.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.list.service.SearchService;
import com.atguigu.gmall.model.list.Goods;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/list")
public class ListApiController {

    @Resource
    private ElasticsearchRestTemplate restTemplate;

    @Resource
    private SearchService searchService;

    /**
     *
     * @return
     */
    @GetMapping("/createIndex")
    public Result createIndex() {
        restTemplate.createIndex(Goods.class);
        restTemplate.putMapping(Goods.class);
        return Result.ok();
    }

    /**
     * 上架商品
     * @return
     */
    @GetMapping(value = "/upperGoods/{skuId}")
    public Result<Goods> upperGoods(@PathVariable(value = "skuId")Long skuId){
        Goods goods = searchService.upperGoods(skuId);
        return Result.ok(goods);
    }

    /**
     * 下架商品
     * @param skuId
     * @return
     */
    @GetMapping(value = "/lowerGoods/{skuId}")
    public Result lowerGoods(@PathVariable(value = "skuId")Long skuId){
        searchService.lowerGoods(skuId);
        return Result.ok();
    }

    /**
     * 更新商品热点值
     * @param skuId
     * @return
     */
    @GetMapping(value = "/incrHotScore/{skuId}")
    public Result incrHotScore(@PathVariable(value = "skuId") Long skuId){
        searchService.incrHotScore(skuId);
        return Result.ok();
    }

    /**
     * 搜索商品
     * @param searchMap
     * @return
     */
    @GetMapping(value = "/search")
    public Map<String,Object> search(Map<String,String> searchMap){
        return searchService.search(searchMap);
    }

}
