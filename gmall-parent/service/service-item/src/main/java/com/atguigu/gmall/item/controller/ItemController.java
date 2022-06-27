package com.atguigu.gmall.item.controller;

import com.atguigu.gmall.item.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping(value = "/item/goods")
public class ItemController {

    @Resource
    private ItemService itemService;

    /**
     * 获取商品的详情
     * @return
     */
    @GetMapping(value = "/detail/{skuId}")
    public Map<String, Object> getGoodsItem(@PathVariable(value = "skuId") Long skuId){
        return itemService.getGoodsItem(skuId);
    }
}
