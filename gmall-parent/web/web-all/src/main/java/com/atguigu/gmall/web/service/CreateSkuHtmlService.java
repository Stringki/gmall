package com.atguigu.gmall.web.service;


/**
 * 生成商品详情页静态化页面的接口类
 */
public interface CreateSkuHtmlService {

    /**
     * 生成商品详情页数据
     * @param skuId
     */
    public void createHtml(Long skuId);

}
