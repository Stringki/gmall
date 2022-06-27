package com.atguigu.gmall.web.service.impl;

import com.atguigu.gmall.item.feign.ItemFeign;
import com.atguigu.gmall.web.service.CreateSkuHtmlService;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.annotation.Resource;
import java.io.File;
import java.io.PrintWriter;
import java.util.Map;

@Service
public class CreateSkuHtmlServiceImpl implements CreateSkuHtmlService {

    @Resource
    private ItemFeign itemFeign;

    @Resource
    private TemplateEngine templateEngine;

    @Override
    public void createHtml(Long skuId) {
        //检验参数
        if (skuId == null) {
            return;
        }
        //获取所有的商品详情需求的数据
        Map<String, Object> goodsItem = itemFeign.getGoodsItem(skuId);
        //定义上下文---容器
        Context context = new Context();
        context.setVariables(goodsItem);
        //生成一个文件对象
        File file = new File("G:/", skuId + ".html");
        try {
            //生成输出对象
            PrintWriter writer = new PrintWriter(file, "UTF-8");
            //生成页面
            templateEngine.process("/item/item", context, writer);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}